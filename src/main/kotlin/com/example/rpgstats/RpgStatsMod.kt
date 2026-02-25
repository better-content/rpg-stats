package com.example.rpgstats

import com.example.rpgstats.common.network.Network
import net.minecraftforge.fml.common.Mod

@Mod(RpgStatsMod.MODID)
object RpgStatsMod {
    const val MODID: String = "rpgstats"

    init {
        Network.init()
        // Everything else is registered via @EventBusSubscriber objects.
    }
}
