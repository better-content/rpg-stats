package com.bettercontent.rpgstats.common.network.packets

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AllocationPolicyTest {
    private val uncapped = mapOf(
        "rpg_stats:attack_damage" to -1,
        "rpg_stats:mining_speed" to -1
    )

    @Test
    fun `spends only newly committed points`() {
        val decision = AllocationPolicy.apply(
            current = mapOf("rpg_stats:attack_damage" to 3),
            unspentPoints = 4,
            requested = mapOf("rpg_stats:attack_damage" to 5, "rpg_stats:mining_speed" to 1),
            maxPointsById = uncapped
        )

        assertEquals(
            AllocationDecision(
                allocations = mapOf("rpg_stats:attack_damage" to 5, "rpg_stats:mining_speed" to 1),
                unspentPoints = 1
            ),
            decision
        )
    }

    @Test
    fun `rejects reducing or omitting a committed allocation`() {
        val current = mapOf("rpg_stats:attack_damage" to 3)

        assertNull(AllocationPolicy.apply(current, 4, mapOf("rpg_stats:attack_damage" to 2), uncapped))
        assertNull(AllocationPolicy.apply(current, 4, emptyMap(), uncapped))
    }

    @Test
    fun `rejects overspending and overflow sized totals`() {
        assertNull(
            AllocationPolicy.apply(
                current = emptyMap(),
                unspentPoints = 2,
                requested = mapOf("rpg_stats:attack_damage" to 3),
                maxPointsById = uncapped
            )
        )
        assertNull(
            AllocationPolicy.apply(
                current = emptyMap(),
                unspentPoints = Int.MAX_VALUE,
                requested = mapOf(
                    "rpg_stats:attack_damage" to Int.MAX_VALUE,
                    "rpg_stats:mining_speed" to Int.MAX_VALUE
                ),
                maxPointsById = uncapped
            )
        )
    }

    @Test
    fun `preserves allocations whose definitions are missing`() {
        val decision = AllocationPolicy.apply(
            current = mapOf("removed_pack:old_stat" to 7),
            unspentPoints = 2,
            requested = mapOf("rpg_stats:mining_speed" to 2),
            maxPointsById = uncapped
        )

        assertEquals(
            mapOf("removed_pack:old_stat" to 7, "rpg_stats:mining_speed" to 2),
            decision?.allocations
        )
        assertEquals(0, decision?.unspentPoints)
    }

    @Test
    fun `optional datapack caps apply only to new investment`() {
        val cap = mapOf("rpg_stats:attack_damage" to 5)

        assertNull(AllocationPolicy.apply(emptyMap(), 6, mapOf("rpg_stats:attack_damage" to 6), cap))
        assertEquals(
            6,
            AllocationPolicy.apply(
                current = mapOf("rpg_stats:attack_damage" to 6),
                unspentPoints = 1,
                requested = mapOf("rpg_stats:attack_damage" to 6),
                maxPointsById = cap
            )?.allocations?.get("rpg_stats:attack_damage")
        )
    }
}
