package com.bettercontent.rpgstats.common.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerStatsTest {
    @Test
    fun `death erases the entire current Life ledger`() {
        val stats = PlayerStats().apply {
            lifePeakLevel = 42
            unspentPoints = 3
            allocations["rpg_stats:impact"] = 8
            allocations["rpg_stats:control"] = 5
        }

        stats.resetForDeath(7)

        assertEquals(7, stats.lifePeakLevel)
        assertEquals(0, stats.unspentPoints)
        assertTrue(stats.allocations.isEmpty())
    }
}
