package com.bettercontent.rpgstats.common.event

import com.bettercontent.rpgstats.RpgStatsMod
import com.bettercontent.rpgstats.common.attribute.MiningSpeedScaling
import com.bettercontent.rpgstats.common.attribute.ModAttributes
import com.bettercontent.rpgstats.common.attribute.StatAttributeProjector
import com.bettercontent.rpgstats.common.data.PlayerStatsProvider
import com.bettercontent.rpgstats.common.data.StatsCap
import com.bettercontent.rpgstats.common.item.ModItems
import com.bettercontent.rpgstats.common.item.StillBeatingHeartData
import com.bettercontent.rpgstats.common.network.Network
import com.bettercontent.rpgstats.common.points.PointAwarder
import com.bettercontent.rpgstats.common.reload.RegistryState
import com.bettercontent.rpgstats.common.reload.StatReloadListener
import com.bettercontent.rpgstats.common.network.packets.S2CStatDefsSync
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommonForgeEvents {
    private const val PENDING_HEARTS_TAG: String = "rpg_stats_pending_hearts"

    @SubscribeEvent
    fun onAttachCaps(event: AttachCapabilitiesEvent<Entity>) {
        val e = event.`object`
        if (e is Player) {
            event.addCapability(StatsCap.KEY, PlayerStatsProvider())
        }
    }

    @SubscribeEvent
    fun onAddReloadListeners(event: AddReloadListenerEvent) {
        event.addListener(StatReloadListener())
    }

    @SubscribeEvent
    fun onClone(event: PlayerEvent.Clone) {
        val oldP = event.original
        val newP = event.entity

        if (event.isWasDeath) {
            newP.getCapability(StatsCap.CAP).ifPresent { newStats ->
                // Wipe, but baseline peak to current XP level to avoid instant refunds on keep-XP rules.
                newStats.resetForDeath((newP as? ServerPlayer)?.experienceLevel ?: 0)
            }
        } else {
            oldP.getCapability(StatsCap.CAP).ifPresent { oldStats ->
                newP.getCapability(StatsCap.CAP).ifPresent { newStats ->
                    newStats.lifePeakLevel = oldStats.lifePeakLevel
                    newStats.unspentPoints = oldStats.unspentPoints
                    newStats.allocations.clear()
                    newStats.allocations.putAll(oldStats.allocations)
                }
            }
        }

        if (event.isWasDeath) {
            transferPendingHearts(oldP, newP)
        }

        if (newP is ServerPlayer) {
            StatAttributeProjector.reapply(newP)
            Network.syncTo(newP)
        }
    }

    @SubscribeEvent
    fun onRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        val player = event.entity as? ServerPlayer ?: return
        deliverPendingHearts(player)
    }

    @SubscribeEvent
    fun onLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val p = event.entity
        if (p is ServerPlayer) {
            val stats = StatsCap.get(p)
            if (stats != null && stats.lifePeakLevel == 0 && stats.totalPointsThisLife() == 0) {
                // Baseline to current (prevents retro points for existing worlds)
                stats.lifePeakLevel = p.experienceLevel
            }

            // Send defs first, then stats snapshot
            Network.sendTo(p, S2CStatDefsSync.fromDefs(RegistryState.activeSnapshot().values.toList()))
            StatAttributeProjector.reapply(p)
            Network.syncTo(p)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onBreakSpeed(event: PlayerEvent.BreakSpeed) {
        val multiplier = event.entity.getAttributeValue(ModAttributes.MINING_SPEED.get())
        event.newSpeed = MiningSpeedScaling.scale(event.newSpeed, multiplier)
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        val p = event.player
        if (p is ServerPlayer) {
            PointAwarder.tick(p)
            StillBeatingHeartAltarHandler.discoverNear(p)
        }
    }

    @SubscribeEvent
    fun onLevelTick(event: TickEvent.LevelTickEvent) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return
        val level = event.level as? net.minecraft.server.level.ServerLevel ?: return
        StillBeatingHeartAltarHandler.tickLevel(level)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onLivingDeathCaptureLevel(event: LivingDeathEvent) {
        val player = event.entity as? ServerPlayer ?: return
        if (player.level().isClientSide || player.isSpectator) return
        if (!StillBeatingHeartAltarHandler.isBloodMagicLoaded()) return

        // Configurable Death clears XP in its normal-priority death handler. Snapshot the
        // level before that happens, but wait until LOWEST to confirm the death survived
        // any cancellation before creating the heart.
        StillBeatingHeartData.captureDeathLevel(player.persistentData, player.experienceLevel)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onLivingDeath(event: LivingDeathEvent) {
        val player = event.entity as? ServerPlayer ?: return
        if (player.level().isClientSide || player.isSpectator) return
        if (!StillBeatingHeartAltarHandler.isBloodMagicLoaded()) return

        val capturedLevel = StillBeatingHeartData.consumeCapturedDeathLevel(player.persistentData)
        val heart = StillBeatingHeartData.createForLevel(capturedLevel, ModItems.STILL_BEATING_HEART.get())
        enqueuePendingHeart(player, heart)
    }

    private fun transferPendingHearts(from: Player, to: Player) {
        val fromData = from.persistentData
        if (!fromData.contains(PENDING_HEARTS_TAG, Tag.TAG_LIST.toInt())) return

        val pending = fromData.getList(PENDING_HEARTS_TAG, Tag.TAG_COMPOUND.toInt()).copy()
        to.persistentData.put(PENDING_HEARTS_TAG, pending)
        fromData.remove(PENDING_HEARTS_TAG)
    }

    private fun enqueuePendingHeart(player: Player, heart: ItemStack) {
        if (heart.isEmpty) return

        val data = player.persistentData
        val pending = if (data.contains(PENDING_HEARTS_TAG, Tag.TAG_LIST.toInt())) {
            data.getList(PENDING_HEARTS_TAG, Tag.TAG_COMPOUND.toInt())
        } else {
            ListTag()
        }

        pending.add(heart.save(CompoundTag()))
        data.put(PENDING_HEARTS_TAG, pending)
    }

    private fun deliverPendingHearts(player: ServerPlayer) {
        val data = player.persistentData
        if (!data.contains(PENDING_HEARTS_TAG, Tag.TAG_LIST.toInt())) return

        val pending = data.getList(PENDING_HEARTS_TAG, Tag.TAG_COMPOUND.toInt())
        if (pending.isEmpty) {
            data.remove(PENDING_HEARTS_TAG)
            return
        }

        val stillPending = ListTag()
        for (i in 0 until pending.size) {
            val stack = ItemStack.of(pending.getCompound(i))
            if (stack.isEmpty) continue

            if (!tryInsertHeart(player, stack)) {
                stillPending.add(stack.save(CompoundTag()))
            }
        }

        if (stillPending.isEmpty) {
            data.remove(PENDING_HEARTS_TAG)
        } else {
            data.put(PENDING_HEARTS_TAG, stillPending)
        }
    }

    private fun tryInsertHeart(player: ServerPlayer, stack: ItemStack): Boolean {
        // Never call Inventory.add here: it merges into the first compatible stack or fills
        // the first empty slot, which changes the player's deliberately arranged layout.
        val destination = PendingHeartSlotPolicy.lastEmptySlot(
            player.inventory.items.indices.map { player.inventory.getItem(it).isEmpty }
        )
        if (destination != null) {
            val slot = destination
            player.inventory.setItem(slot, stack.copy())
            return true
        }

        val enderChest = player.enderChestInventory
        for (slot in 0 until enderChest.containerSize) {
            if (!enderChest.getItem(slot).isEmpty) continue
            enderChest.setItem(slot, stack.copy())
            return true
        }

        return false
    }
}

internal object PendingHeartSlotPolicy {
    fun lastEmptySlot(emptySlots: List<Boolean>): Int? = emptySlots.indices.reversed().firstOrNull { emptySlots[it] }
}
