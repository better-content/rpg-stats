package com.bettercontent.rpgstats.common.item

import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class HeartFragmentDataTest {
    @Test fun `final death curve is zero at zero and strictly increases for reachable positive levels`() {
        assertEquals(0, HeartFragmentData.fragmentsForLevel(0))
        val values = (1..30).map(HeartFragmentData::fragmentsForLevel)
        values.zipWithNext().forEach { (lower, higher) -> assertTrue(higher > lower) }
        assertEquals(listOf(1L, 2L, 3L, 4L, 6L), values.take(5))
        assertEquals(124, HeartFragmentData.fragmentsForLevel(20))
        assertEquals(BigInteger("147573952589676412924"), HeartFragmentData.fragmentsForLevelBig(260))
        assertEquals(62, HeartFragmentData.fragmentsForLevel(20, 2))
        assertEquals(721, HeartFragmentData.fragmentsForLevel(30))
    }

    @Test fun `overflow is explicit instead of a silent fragment clamp`() {
        assertFailsWith<IllegalArgumentException> { HeartFragmentData.fragmentsForLevelBig(Int.MAX_VALUE) }
    }

    @Test fun `installed fragment output is linear`() {
        assertEquals(0, HeartFragmentData.lpPerTick(0))
        assertEquals(32, HeartFragmentData.lpPerTick(32))
    }
}
