package com.example.rpgstats.kubejs

import com.example.rpgstats.common.heart.HeartRequirements
import com.example.rpgstats.common.heart.HeartTypeDefinition
import com.example.rpgstats.common.heart.IntBounds
import dev.latvian.mods.kubejs.event.StartupEventJS
import net.minecraft.resources.ResourceLocation
import kotlin.math.max

class HeartTypesStartupEventJS : StartupEventJS() {
    private val builders = mutableListOf<HeartTypeBuilderJS>()

    fun heartType(id: String): HeartTypeBuilderJS {
        val typeId = parseId(id) ?: error("Invalid heart type id '$id'")
        val builder = HeartTypeBuilderJS(typeId)
        builders += builder
        return builder
    }

    fun buildDefinitions(): List<HeartTypeDefinition> = builders.mapNotNull { it.buildOrNull() }

    private fun parseId(raw: String): ResourceLocation? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return if (':' in trimmed) {
            ResourceLocation.tryParse(trimmed)
        } else {
            ResourceLocation.tryParse("kubejs:$trimmed")
        }
    }
}

class HeartTypeBuilderJS(
    private val typeId: ResourceLocation
) {
    private var outputItemId: ResourceLocation = ResourceLocation(typeId.namespace, "${typeId.path}_heart")
    private var catalystSpec: String = ""
    private var assetTexture: ResourceLocation = ResourceLocation(typeId.namespace, "item/hearts/${typeId.path}")
    private var displayName: String? = null
    private var consumeCatalyst: Boolean = true
    private var channelTime: Int = 40
    private var priority: Int = 0
    private var levelMin: Int? = null
    private var levelMax: Int? = null
    private var ritualTierMin: Int? = null
    private var ritualTierMax: Int? = null
    private var dimension: ResourceLocation? = null
    private var deathCause: String? = null
    private val statBounds = linkedMapOf<ResourceLocation, MutableBounds>()

    fun item(id: String): HeartTypeBuilderJS {
        val parsed = parseId(id) ?: error("Invalid output item id '$id'")
        outputItemId = parsed
        return this
    }

    fun asset(texture: String): HeartTypeBuilderJS {
        val parsed = parseTexture(texture) ?: error("Invalid asset texture '$texture'")
        assetTexture = parsed
        return this
    }

    fun displayName(name: String): HeartTypeBuilderJS {
        displayName = name.trim().ifEmpty { null }
        return this
    }

    fun catalyst(spec: String): HeartTypeBuilderJS {
        catalystSpec = spec.trim()
        return this
    }

    fun consumeCatalyst(consume: Boolean): HeartTypeBuilderJS {
        consumeCatalyst = consume
        return this
    }

    fun channelTime(ticks: Int): HeartTypeBuilderJS {
        channelTime = max(1, ticks)
        return this
    }

    fun priority(value: Int): HeartTypeBuilderJS {
        priority = value
        return this
    }

    fun requireLevelMin(value: Int): HeartTypeBuilderJS {
        levelMin = value
        return this
    }

    fun requireLevelMax(value: Int): HeartTypeBuilderJS {
        levelMax = value
        return this
    }

    fun requireRitualTierMin(value: Int): HeartTypeBuilderJS {
        ritualTierMin = value
        return this
    }

    fun requireRitualTierMax(value: Int): HeartTypeBuilderJS {
        ritualTierMax = value
        return this
    }

    fun requireDimension(id: String): HeartTypeBuilderJS {
        dimension = parseId(id) ?: error("Invalid dimension id '$id'")
        return this
    }

    fun requireDeathCause(cause: String): HeartTypeBuilderJS {
        deathCause = cause.trim().ifEmpty { null }
        return this
    }

    fun requireStatMin(statId: String, value: Int): HeartTypeBuilderJS {
        statBounds.getOrPut(parseId(statId) ?: error("Invalid stat id '$statId'")) { MutableBounds() }.min = value
        return this
    }

    fun requireStatMax(statId: String, value: Int): HeartTypeBuilderJS {
        statBounds.getOrPut(parseId(statId) ?: error("Invalid stat id '$statId'")) { MutableBounds() }.max = value
        return this
    }

    fun buildOrNull(): HeartTypeDefinition? {
        if (catalystSpec.isBlank()) return null
        val reqStats = statBounds.mapValues { (_, bounds) -> IntBounds(bounds.min, bounds.max) }
        val req = HeartRequirements(
            level = IntBounds(levelMin, levelMax),
            ritualTier = IntBounds(ritualTierMin, ritualTierMax),
            dimension = dimension,
            deathCause = deathCause,
            stats = reqStats
        )
        return HeartTypeDefinition(
            typeId = typeId,
            outputItemId = outputItemId,
            catalyst = catalystSpec,
            assetTexture = assetTexture,
            displayName = displayName,
            consumeCatalyst = consumeCatalyst,
            channelTime = channelTime,
            priority = priority,
            requirements = req
        )
    }

    private fun parseId(raw: String): ResourceLocation? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return if (':' in trimmed) {
            ResourceLocation.tryParse(trimmed)
        } else {
            ResourceLocation.tryParse("kubejs:$trimmed")
        }
    }

    private fun parseTexture(raw: String): ResourceLocation? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null
        return if (':' in trimmed) {
            ResourceLocation.tryParse(trimmed)
        } else {
            ResourceLocation.tryParse("kubejs:$trimmed")
        }
    }

    private class MutableBounds(
        var min: Int? = null,
        var max: Int? = null
    )
}
