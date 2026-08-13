package com.bettercontent.rpgstats.common.reload

import com.bettercontent.rpgstats.common.config.json.StatDefinition
import net.minecraft.resources.ResourceLocation

object RegistryState {
    @Volatile private var defs: Map<ResourceLocation, StatDefinition> = emptyMap()

    fun set(newDefs: Map<ResourceLocation, StatDefinition>) {
        defs = newDefs
    }

    fun snapshot(): Map<ResourceLocation, StatDefinition> = defs
}
