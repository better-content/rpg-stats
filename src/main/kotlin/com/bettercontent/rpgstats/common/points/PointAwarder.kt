package com.bettercontent.rpgstats.common.points

import com.bettercontent.rpgstats.common.data.StatsCap
import com.bettercontent.rpgstats.common.compat.ThreadsBridge
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
            if (stats.lifeAllocationEpisode == null) stats.lifeAllocationEpisode = ThreadsBridge.newEpisode(player)
            ThreadsBridge.available(player, stats.lifeAllocationEpisode!!)
            Network.syncTo(player)
        }
    }
}
