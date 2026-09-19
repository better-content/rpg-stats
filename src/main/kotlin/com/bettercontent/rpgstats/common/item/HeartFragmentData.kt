package com.bettercontent.rpgstats.common.item

import com.bettercontent.rpgstats.common.config.HeartFragmentConfig

import kotlin.math.ceil
import kotlin.math.pow

/** Death-reward math. Values are counts, and are deliberately not stored on fragment stacks. */
object HeartFragmentData {
    const val DEFAULT_ENTITLEMENT_SCALE: Int = HeartFragmentConfig.DEFAULT_ENTITLEMENT_SCALE
    const val LEVELS_PER_DOUBLING: Int = 4
    const val LP_PER_FRAGMENT_PER_TICK: Int = 1

    fun fragmentsForLevel(level: Int): Long = fragmentsForLevel(level, HeartFragmentConfig.entitlementScale())

    internal fun fragmentsForLevel(level: Int, scale: Int): Long {
        require(scale > 0) { "Heart fragment entitlement scale must be positive" }
        if (level <= 0) return 0
        val doubled = 2.0.pow(level.toDouble() / LEVELS_PER_DOUBLING)
        val value = scale.toDouble() * (doubled - 1.0)
        require(value.isFinite() && value >= 0.0 && value <= Long.MAX_VALUE.toDouble()) {
            "Heart fragment entitlement overflows at level $level"
        }
        return ceil(value).toLong().coerceAtLeast(1)
    }

    fun lpPerTick(installedFragments: Long): Long {
        require(installedFragments >= 0) { "Installed heart fragments cannot be negative" }
        return Math.multiplyExact(installedFragments, LP_PER_FRAGMENT_PER_TICK.toLong())
    }
}
