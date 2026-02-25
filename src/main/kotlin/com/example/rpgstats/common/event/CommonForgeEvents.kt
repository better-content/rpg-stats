package com.example.rpgstats.common.event

import com.example.rpgstats.RpgStatsMod
import com.example.rpgstats.common.attribute.StatAttributeProjector
import com.example.rpgstats.common.data.PlayerStatsProvider
import com.example.rpgstats.common.data.StatsCap
import com.example.rpgstats.common.network.Network
import com.example.rpgstats.common.points.PointAwarder
import com.example.rpgstats.common.reload.RegistryState
import com.example.rpgstats.common.reload.StatReloadListener
import com.example.rpgstats.common.network.packets.S2CStatDefsSync
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommonForgeEvents {

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

        if (newP is ServerPlayer) {
            StatAttributeProjector.reapply(newP)
            Network.syncTo(newP)
        }
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
        }
    }
}
