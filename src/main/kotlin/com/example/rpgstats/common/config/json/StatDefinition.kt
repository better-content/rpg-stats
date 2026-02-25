package com.example.rpgstats.common.config.json

import net.minecraft.resources.ResourceLocation

data class StatDefinition(
    val id: ResourceLocation,
    val nameKey: String,
    val maxPoints: Int = -1,
    val effects: List<StatEffect> = emptyList(),
    val icon: String = "",
    val color: String = "#FFFFFF"
)
