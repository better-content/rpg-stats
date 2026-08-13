package com.bettercontent.rpgstats.common.points

import com.bettercontent.rpgstats.common.data.StatsCap
import com.bettercontent.rpgstats.common.network.Network
import net.minecraft.server.level.ServerPlayer

object PointAwarder {

    /**
     * Award +1 point for each level above the lifePeakLevel.
     * This runs on server tick.
     */
    fun tick(player: ServerPlayer) {
        val stats = StatsCap.get(player) ?: return
        val cur = player.experienceLevel
        if (cur > stats.lifePeakLevel) {
            val diff = cur - stats.lifePeakLevel
            stats.lifePeakLevel = cur
            stats.unspentPoints += diff
            Network.syncTo(player)
        }
    }

    /**
     * Reset the "life" ledger.
     * Baselines peak to current level so a keep-XP-on-death rule doesn't instantly refund points.
     */
    fun resetLife(player: ServerPlayer) {
        val stats = StatsCap.get(player) ?: return
        stats.lifePeakLevel = player.experienceLevel
        stats.unspentPoints = 0
        stats.allocations.clear()
        Network.syncTo(player)
    }
}
