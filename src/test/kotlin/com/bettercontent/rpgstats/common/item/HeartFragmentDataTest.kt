package com.bettercontent.rpgstats.common.item

import kotlin.test.Test
import kotlin.test.assertEquals

class HeartFragmentDataTest {
    @Test fun `final death fragments use held level exponential without a zero reward`() {
        assertEquals(0, HeartFragmentData.fragmentsForLevel(0))
        assertEquals(1, HeartFragmentData.fragmentsForLevel(1))
        assertEquals(2, HeartFragmentData.fragmentsForLevel(5))
        assertEquals(32, HeartFragmentData.fragmentsForLevel(20))
        assertEquals(181, HeartFragmentData.fragmentsForLevel(30))
    }

    @Test fun `installed fragment output is linear`() {
        assertEquals(0, HeartFragmentData.lpPerTick(0))
        assertEquals(32, HeartFragmentData.lpPerTick(32))
    }
}
