package com.bettercontent.rpgstats.common.compat

import net.minecraft.server.level.ServerPlayer

/** Optional Threads integration based only on accepted server-side Life allocations. */
object ThreadsBridge {
    fun newEpisode(player: ServerPlayer): String = "${player.uuid}:life:${player.server.tickCount}"

    fun available(player: ServerPlayer, token: String) = emit(player, "life_allocation", "available", token)
    fun spent(player: ServerPlayer, token: String) = emit(player, "life_allocation", "spent", token)

    private fun emit(player: ServerPlayer, type: String, value: String, correlation: String) {
        try {
            val api = Class.forName("com.bettercontent.threads.api.ThreadSignals")
            api.getMethod("emit", ServerPlayer::class.java, String::class.java, String::class.java, String::class.java)
                .invoke(null, player, type, value, correlation)
        } catch (_: ClassNotFoundException) {
        } catch (_: NoSuchMethodException) {
        } catch (_: ReflectiveOperationException) {
        }
    }
}
