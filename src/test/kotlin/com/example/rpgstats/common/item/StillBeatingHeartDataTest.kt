package com.example.rpgstats.common.item

import com.example.rpgstats.TestMinecraftBootstrap
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StillBeatingHeartDataTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            TestMinecraftBootstrap.bootstrap()
        }
    }

    @Test
    fun `new hearts write only schema and level`() {
        val stack = StillBeatingHeartData.createForLevel(17, Items.DIAMOND)
        val data = assertNotNull(StillBeatingHeartData.getData(stack))

        assertEquals(2, data.getInt("schema_version"))
        assertEquals(17, data.getInt("level"))
        assertEquals(setOf("schema_version", "level"), data.allKeys)
    }

    @Test
    fun `level reads new and legacy schemas`() {
        val modern = StillBeatingHeartData.createForLevel(24, Items.DIAMOND)
        assertEquals(24, StillBeatingHeartData.getLevel(modern))

        val legacyData = CompoundTag()
        val legacyPlayer = CompoundTag()
        legacyPlayer.putInt("experience_level", 31)
        legacyData.put("player", legacyPlayer)
        val legacy = ItemStack(Items.DIAMOND)
        legacy.orCreateTag.put(StillBeatingHeartData.DATA_TAG, legacyData)

        assertEquals(31, StillBeatingHeartData.getLevel(legacy))
    }

    @Test
    fun `invalid stacks are not valid blood sources`() {
        assertFalse(StillBeatingHeartData.isValid(ItemStack.EMPTY))
        assertFalse(StillBeatingHeartData.isValid(ItemStack(Items.DIAMOND)))
        assertEquals(0, StillBeatingHeartData.getLevel(ItemStack(Items.DIAMOND)))
    }

    @Test
    fun `lp formula is stable and clamps high levels`() {
        assertEquals(5, StillBeatingHeartData.lpPerTick(0))
        assertEquals(5, StillBeatingHeartData.lpPerTick(1))
        assertEquals(10, StillBeatingHeartData.lpPerTick(10))
        assertEquals(20, StillBeatingHeartData.lpPerTick(20))
        assertEquals(4096, StillBeatingHeartData.lpPerTick(200))
    }

    @Test
    fun `negative levels are normalized on write`() {
        val stack = StillBeatingHeartData.createForLevel(-10, Items.DIAMOND)
        assertTrue(StillBeatingHeartData.isValid(stack))
        assertEquals(0, StillBeatingHeartData.getLevel(stack))
    }
}
