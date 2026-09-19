package com.bettercontent.rpgstats.common.points

import com.bettercontent.rpgstats.common.data.PlayerStats

/** Server-side plan validation and one-point-at-a-time application. */
object AutoAllocationPlan {
    const val MAX_ENTRIES = 64

    fun replace(stats: PlayerStats, enabled: Boolean, requested: List<String>, admitted: Map<String, Int>): Boolean {
        if (requested.size > MAX_ENTRIES || requested.distinct().size != requested.size) return false
        if (requested.any { it !in admitted }) return false
        stats.autoAllocationPlan.clear()
        stats.autoAllocationPlan += requested
        stats.autoAllocationEnabled = enabled && requested.isNotEmpty()
        stats.autoAllocationCursor = if (requested.isEmpty()) 0 else stats.autoAllocationCursor.mod(requested.size)
        return true
    }

    /** Spend each available point at most once. Invalid, removed, or capped choices are skipped. */
    fun apply(stats: PlayerStats, admitted: Map<String, Int>, budget: Int = stats.unspentPoints): Map<String, Int> {
        if (!stats.autoAllocationEnabled || stats.unspentPoints <= 0 || stats.autoAllocationPlan.isEmpty()) return emptyMap()
        val spent = linkedMapOf<String, Int>()
        var attemptsWithoutPurchase = 0
        var remaining = budget.coerceIn(0, stats.unspentPoints)
        while (remaining > 0 && attemptsWithoutPurchase < stats.autoAllocationPlan.size) {
            val index = stats.autoAllocationCursor.mod(stats.autoAllocationPlan.size)
            val id = stats.autoAllocationPlan[index]
            stats.autoAllocationCursor = (index + 1).mod(stats.autoAllocationPlan.size)
            val maximum = admitted[id]
            val current = stats.allocations[id] ?: 0
            if (maximum == null || (maximum >= 0 && current >= maximum)) {
                attemptsWithoutPurchase++
                continue
            }
            stats.allocations[id] = current + 1
            stats.unspentPoints--
            remaining--
            spent[id] = (spent[id] ?: 0) + 1
            attemptsWithoutPurchase = 0
        }
        return spent
    }
}
