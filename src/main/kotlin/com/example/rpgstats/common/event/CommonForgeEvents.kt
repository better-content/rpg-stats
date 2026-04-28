package com.example.rpgstats.common.event

import com.example.rpgstats.RpgStatsMod
import com.example.rpgstats.common.attribute.StatAttributeProjector
import com.example.rpgstats.common.data.PlayerStatsProvider
import com.example.rpgstats.common.data.StatsCap
import com.example.rpgstats.common.item.StillBeatingHeartData
import com.example.rpgstats.common.network.Network
import com.example.rpgstats.common.points.PointAwarder
import com.example.rpgstats.common.reload.RegistryState
import com.example.rpgstats.common.reload.StatReloadListener
import com.example.rpgstats.common.network.packets.S2CStatDefsSync
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
    private const val PENDING_HEARTS_TAG: String = "rpgstats_pending_hearts"

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

        oldP.getCapability(StatsCap.CAP).ifPresent { oldStats ->
            newP.getCapability(StatsCap.CAP).ifPresent { newStats ->
                if (event.isWasDeath) {
                    // Wipe, but baseline peak to current XP level to avoid instant refunds on keep-XP rules.
                    newStats.lifePeakLevel = (newP as? ServerPlayer)?.experienceLevel ?: 0
                    newStats.unspentPoints = 0
                    newStats.allocations.clear()
                } else {
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

            deliverPendingHearts(p)

            // Send defs first, then stats snapshot
            Network.sendTo(p, S2CStatDefsSync.fromDefs(RegistryState.snapshot().values.toList()))
            StatAttributeProjector.reapply(p)
            Network.syncTo(p)
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: TickEvent.PlayerTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        val p = event.player
        if (p is ServerPlayer) {
            PointAwarder.tick(p)
            deliverPendingHearts(p)
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onLivingDeath(event: LivingDeathEvent) {
        val player = event.entity as? ServerPlayer ?: return
        if (player.level().isClientSide || player.isSpectator) return

        val heart = StillBeatingHeartData.create(player, event.source)
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
        val remaining = stack.copy()
        if (player.inventory.add(remaining) || remaining.isEmpty) return true

        val enderChest = player.enderChestInventory
        for (slot in 0 until enderChest.containerSize) {
            if (!enderChest.getItem(slot).isEmpty) continue
            enderChest.setItem(slot, remaining.copy())
            return true
        }

        return false
    }
}
