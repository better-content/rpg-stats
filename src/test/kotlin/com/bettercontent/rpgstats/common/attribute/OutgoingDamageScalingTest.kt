package com.bettercontent.rpgstats.common.attribute

import kotlin.test.Test
import kotlin.test.assertEquals

class OutgoingDamageScalingTest {
    @Test
    fun `percentage multiplier preserves a base damage amount without flat injection`() {
        assertEquals(1.0f, OutgoingDamageScaling.multiplier(0.0))
        assertEquals(1.3f, OutgoingDamageScaling.multiplier(0.3))
        assertEquals(13.0f, OutgoingDamageScaling.scaleAmount(10.0f, OutgoingDamageScaling.multiplier(0.3)))
    }

    @Test
    fun `negative values cannot make outgoing damage negative`() {
        assertEquals(0.0f, OutgoingDamageScaling.multiplier(-2.0))
    }
}
