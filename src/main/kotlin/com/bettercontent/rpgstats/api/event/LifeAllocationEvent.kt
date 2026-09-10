package com.bettercontent.rpgstats.api.event

import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.eventbus.api.Event

/** Posted after the server makes Life allocation points available or accepts their expenditure. */
class LifeAllocationEvent(
    val player: ServerPlayer,
    val state: State,
    val episodeId: String
) : Event() {
    enum class State {
        AVAILABLE,
        SPENT
    }
}
