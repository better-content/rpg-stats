package com.bettercontent.rpgstats.common.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Durable final-death rewards.  Pending rows and completion receipts live beside a player's
 * inventory in playerdata, so normal player saves commit delivery and receipt together.
 */
object HeartFragmentEntitlements {
    private const val CAPTURE_TAG = "rpg_stats_captured_heart_entitlement"
    private const val PENDING_TAG = "rpg_stats_pending_heart_fragment_entitlements"
    private const val COMPLETED_TAG = "rpg_stats_completed_heart_fragment_entitlements"
    private const val ID_TAG = "id"
    private const val COUNT_TAG = "count"
    private const val LEVEL_TAG = "level"
    private const val LEGACY_PREFIX = "legacy-personalized-heart-v1:"

    data class Entitlement(val id: UUID, val fragments: Long)

    fun captureFinalDeath(data: CompoundTag, heldLevel: Int, id: UUID = UUID.randomUUID()) {
        val capture = CompoundTag()
        capture.putString(ID_TAG, id.toString())
        capture.putInt(LEVEL_TAG, heldLevel.coerceAtLeast(0))
        data.put(CAPTURE_TAG, capture)
    }

    fun discardCapturedDeath(data: CompoundTag) {
        data.remove(CAPTURE_TAG)
    }

    /** Called only after the low-priority death event confirms this life actually ended. */
    fun finalizeCapturedDeath(data: CompoundTag): Entitlement? {
        if (!data.contains(CAPTURE_TAG, Tag.TAG_COMPOUND.toInt())) return null
        val capture = data.getCompound(CAPTURE_TAG)
        val id = capture.uuid(ID_TAG) ?: run {
            data.remove(CAPTURE_TAG)
            return null
        }
        val fragments = HeartFragmentData.fragmentsForLevel(capture.getInt(LEVEL_TAG))
        data.remove(CAPTURE_TAG)
        return Entitlement(id, fragments).takeIf { it.fragments > 0 }
    }

    /** Returns false for a retry/replay of an existing UUID without mutating persistent state. */
    fun enqueue(data: CompoundTag, entitlement: Entitlement): Boolean {
        if (entitlement.fragments <= 0 || containsId(data, COMPLETED_TAG, entitlement.id) || containsId(data, PENDING_TAG, entitlement.id)) {
            return false
        }
        val pending = list(data, PENDING_TAG)
        pending.add(row(entitlement.id, entitlement.fragments))
        data.put(PENDING_TAG, pending)
        return true
    }

    fun pending(data: CompoundTag): List<Entitlement> = (0 until list(data, PENDING_TAG).size).mapNotNull { index ->
        val row = list(data, PENDING_TAG).getCompound(index)
        row.uuid(ID_TAG)?.let { id -> Entitlement(id, row.getLong(COUNT_TAG).coerceAtLeast(0)) }
    }.filter { it.fragments > 0 }

    /** Replaces a pending row after a partial inventory insert, or records its exact receipt. */
    fun recordDelivery(data: CompoundTag, id: UUID, delivered: Long): Boolean {
        if (delivered <= 0) return false
        val pending = list(data, PENDING_TAG)
        for (index in 0 until pending.size) {
            val row = pending.getCompound(index)
            if (row.uuid(ID_TAG) != id) continue
            val remaining = row.getLong(COUNT_TAG).coerceAtLeast(0)
            require(delivered <= remaining) { "Cannot deliver more heart fragments than entitlement $id owns" }
            if (delivered == remaining) {
                pending.removeAt(index)
                data.put(PENDING_TAG, pending)
                list(data, COMPLETED_TAG).also { completed ->
                    completed.add(row(id, 0))
                    data.put(COMPLETED_TAG, completed)
                }
            } else {
                row.putLong(COUNT_TAG, remaining - delivered)
                pending[index] = row
                data.put(PENDING_TAG, pending)
            }
            return true
        }
        return false
    }

    /**
     * Converts old pending personalized-heart rows exactly once.  The deterministic UUID means a
     * crash between enqueue and removing the legacy list retries harmlessly on the next login.
     */
    fun migrateLegacyPending(data: CompoundTag, stacks: List<LegacyHeart>): Int {
        var migrated = 0
        stacks.forEachIndexed { index, legacy ->
            val fragments = HeartFragmentData.fragmentsForLevel(legacy.level)
            if (fragments <= 0) return@forEachIndexed
            val id = UUID.nameUUIDFromBytes((LEGACY_PREFIX + index + ':' + legacy.fingerprint).toByteArray(StandardCharsets.UTF_8))
            if (enqueue(data, Entitlement(id, fragments))) migrated += 1
        }
        return migrated
    }

    data class LegacyHeart(val level: Int, val fingerprint: String)

    private fun containsId(data: CompoundTag, key: String, id: UUID): Boolean =
        (0 until list(data, key).size).any { index -> list(data, key).getCompound(index).uuid(ID_TAG) == id }

    private fun list(data: CompoundTag, key: String): ListTag =
        if (data.contains(key, Tag.TAG_LIST.toInt())) data.getList(key, Tag.TAG_COMPOUND.toInt()) else ListTag()

    private fun row(id: UUID, count: Long): CompoundTag = CompoundTag().also {
        it.putString(ID_TAG, id.toString())
        it.putLong(COUNT_TAG, count.coerceAtLeast(0))
    }

    private fun CompoundTag.uuid(key: String): UUID? = runCatching { UUID.fromString(getString(key)) }.getOrNull()
}
