package com.bettercontent.rpgstats.common.attribute

import kotlin.test.Test
import kotlin.test.assertEquals

class EfficiencyScalingTest {
    @Test
    fun `efficiency divides exhaustion without ever amplifying it`() {
        assertEquals(4.0f, EfficiencyScaling.scale(4.0f, 0.0), 0.0001f)
        assertEquals(4.0f, EfficiencyScaling.scale(4.0f, 1.0), 0.0001f)
        assertEquals(2.0f, EfficiencyScaling.scale(4.0f, 2.0), 0.0001f)
        assertEquals(4.0f / 3.0f, EfficiencyScaling.scale(4.0f, 3.0), 0.0001f)
    }

    @Test
    fun `fifty points at four percent reaches three hundred percent efficiency`() {
        val efficiency = 1.0 + 50 * 0.04
        assertEquals(3.0, efficiency, 0.0001)
        assertEquals(1.0f, EfficiencyScaling.scale(3.0f, efficiency), 0.0001f)
    }
}
