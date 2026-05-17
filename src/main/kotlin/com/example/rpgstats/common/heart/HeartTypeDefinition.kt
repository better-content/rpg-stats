package com.example.rpgstats.common.heart

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation

data class IntBounds(
    val min: Int? = null,
    val max: Int? = null
) {
    fun matches(value: Int): Boolean {
        if (min != null && value < min) return false
        if (max != null && value > max) return false
        return true
    }
}

data class HeartRequirements(
    val level: IntBounds = IntBounds(),
    val ritualTier: IntBounds = IntBounds(),
    val dimension: ResourceLocation? = null,
    val deathCause: String? = null,
    val stats: Map<ResourceLocation, IntBounds> = emptyMap()
) {
    fun matches(data: CompoundTag): Boolean {
        val levelValue = if (data.contains("level", Tag.TAG_INT.toInt())) {
            data.getInt("level")
        } else {
            data.getCompound("player").getInt("experience_level")
        }
        if (!level.matches(levelValue)) return false

        val ritualTag = data.getCompound("ritual")
        val ritualTierValue = if (ritualTag.getBoolean("performed")) ritualTag.getInt("tier") else 0
        if (!ritualTier.matches(ritualTierValue)) return false

        if (dimension != null) {
            val dimRaw = data.getCompound("location").getString("dimension")
            val dim = ResourceLocation.tryParse(dimRaw) ?: return false
            if (dim != dimension) return false
        }

        if (!deathCause.isNullOrBlank()) {
            val cause = data.getCompound("death").getString("cause_id")
            if (!cause.equals(deathCause, ignoreCase = true)) return false
        }

        if (stats.isEmpty()) return true
        val pointsByStat = readStatPoints(data)
        for ((statId, bounds) in stats) {
            val value = pointsByStat[statId] ?: 0
            if (!bounds.matches(value)) return false
        }
        return true
    }

    private fun readStatPoints(data: CompoundTag): Map<ResourceLocation, Int> {
        val out = mutableMapOf<ResourceLocation, Int>()
        val entries = data.getCompound("rpgstats").getList("entries", Tag.TAG_COMPOUND.toInt())
        for (i in 0 until entries.size) {
            val entry = entries.getCompound(i)
            val id = ResourceLocation.tryParse(entry.getString("id")) ?: continue
            out[id] = if (entry.contains("effective_points", Tag.TAG_INT.toInt())) {
                entry.getInt("effective_points")
            } else {
                entry.getInt("points")
            }
        }
        return out
    }
}

data class HeartTypeDefinition(
    val typeId: ResourceLocation,
    val outputItemId: ResourceLocation,
    val catalyst: String,
    val assetTexture: ResourceLocation,
    val displayName: String?,
    val consumeCatalyst: Boolean,
    val channelTime: Int,
    val priority: Int,
    val requirements: HeartRequirements
)
