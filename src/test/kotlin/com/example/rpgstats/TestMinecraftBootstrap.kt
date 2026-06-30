package com.example.rpgstats

import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap

object TestMinecraftBootstrap {
    private var bootstrapped = false

    fun bootstrap() {
        if (bootstrapped) return
        SharedConstants.tryDetectVersion()
        Bootstrap.bootStrap()
        bootstrapped = true
    }
}
