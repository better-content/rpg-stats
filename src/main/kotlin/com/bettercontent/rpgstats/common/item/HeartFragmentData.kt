package com.bettercontent.rpgstats.common.item

import kotlin.math.floor
import kotlin.math.pow

/** Death-reward math. Values are counts, and are deliberately not stored on fragment stacks. */
object HeartFragmentData {
    const val LEVELS_PER_DOUBLING: Int = 4
    const val LP_PER_FRAGMENT_PER_TICK: Int = 1

    fun fragmentsForLevel(level: Int): Long {
        if (level <= 0) return 0
        val value = 2.0.pow(level.toDouble() / LEVELS_PER_DOUBLING)
        require(value.isFinite() && value <= Long.MAX_VALUE.toDouble()) { "Heart fragment entitlement overflows at level $level" }
        return floor(value).toLong().coerceAtLeast(1)
    }

    fun lpPerTick(installedFragments: Long): Long {
        require(installedFragments >= 0) { "Installed heart fragments cannot be negative" }
        return Math.multiplyExact(installedFragments, LP_PER_FRAGMENT_PER_TICK.toLong())
    }
}
