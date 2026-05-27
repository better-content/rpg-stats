package com.example.rpgstats.common.curve

import com.example.rpgstats.common.config.json.CurveDef
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class CurvesTest {
    @Test
    fun `hyperbola clamps negative points and approaches cap`() {
        val curve = CurveDef(type = "hyperbola", cap = 10.0, k = 5.0, min = 0.0, max = 10.0)

        assertEquals(0.0, Curves.eval(-3, curve))
        assertEquals(5.0, Curves.eval(5, curve))
        assertEquals(8.0, Curves.eval(20, curve))
    }

    @Test
    fun `exponential uses positive fallback k and bounds output`() {
        val curve = CurveDef(type = "exp", cap = 100.0, k = 0.0, min = 0.0, max = 50.0)
        val expected = 100.0 * (1.0 - exp(-1.0))

        assertEquals(50.0, Curves.eval(1, curve))
        assertEquals(expected.coerceIn(0.0, 50.0), Curves.eval(1, curve))
    }

    @Test
    fun `linear scales by points and clamps to min max`() {
        val curve = CurveDef(type = "linear", perPoint = 2.5, min = 0.0, max = 10.0)

        assertEquals(7.5, Curves.eval(3, curve))
        assertEquals(10.0, Curves.eval(12, curve))
    }
}
