package com.example.rpgstats.kubejs

import dev.latvian.mods.kubejs.event.EventGroup
import dev.latvian.mods.kubejs.event.EventHandler

object RpgStatsKubeEvents {
    val GROUP: EventGroup = EventGroup.of("RPGStatsEvents")
    val HEART_TYPES: EventHandler = GROUP.startup("heartTypes") { HeartTypesStartupEventJS::class.java }
}
