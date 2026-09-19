package com.bettercontent.rpgstats.common.item

import net.minecraft.nbt.CompoundTag
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HeartFragmentEntitlementsTest {
    @Test
    fun `final death capture has one UUID entitlement across retry and reload`() {
        val playerData = CompoundTag()
        val id = UUID.randomUUID()
        HeartFragmentEntitlements.captureFinalDeath(playerData, 20, id)

        val entitlement = HeartFragmentEntitlements.finalizeCapturedDeath(playerData)!!
        assertEquals(id, entitlement.id)
        assertEquals(124, entitlement.fragments)
        assertTrue(HeartFragmentEntitlements.enqueue(playerData, entitlement))
        assertFalse(HeartFragmentEntitlements.enqueue(playerData, entitlement))
        assertEquals(listOf(entitlement), HeartFragmentEntitlements.pending(playerData.copy()))
    }

    @Test
    fun `partial inventory delivery retains only the undistributed amount`() {
        val playerData = CompoundTag()
        val entitlement = HeartFragmentEntitlements.Entitlement(UUID.randomUUID(), 80)
        assertTrue(HeartFragmentEntitlements.enqueue(playerData, entitlement))

        assertTrue(HeartFragmentEntitlements.recordDelivery(playerData, entitlement.id, 64))
        assertEquals(listOf(entitlement.copy(fragments = 16)), HeartFragmentEntitlements.pending(playerData.copy()))
        assertTrue(HeartFragmentEntitlements.recordDelivery(playerData, entitlement.id, 16))
        assertEquals(emptyList(), HeartFragmentEntitlements.pending(playerData))
        assertFalse(HeartFragmentEntitlements.enqueue(playerData, entitlement))
    }

    @Test
    fun `legacy personalized heart migration derives an idempotent UUID`() {
        val data = CompoundTag()
        val legacy = listOf(HeartFragmentEntitlements.LegacyHeart(level = 20, fingerprint = "old-heart-nbt"))

        assertEquals(1, HeartFragmentEntitlements.migrateLegacyPending(data, legacy))
        assertEquals(0, HeartFragmentEntitlements.migrateLegacyPending(data, legacy))
        assertEquals(124, HeartFragmentEntitlements.pending(data).single().fragments)
    }
}
