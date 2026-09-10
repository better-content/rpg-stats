package com.bettercontent.rpgstats

import com.bettercontent.rpgstats.common.item.ModItems
import com.bettercontent.rpgstats.common.attribute.ModAttributes
import com.bettercontent.rpgstats.common.network.Network
import com.bettercontent.rpgstats.common.sound.ModSounds
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

@Mod(RpgStatsMod.MODID)
object RpgStatsMod {
    const val MODID: String = "rpg_stats"

    init {
        val modBus = KotlinModLoadingContext.get().getKEventBus()
        ModAttributes.register(modBus)
        ModItems.register(modBus)
        ModSounds.register(modBus)
        Network.init()
        // Everything else is registered via @EventBusSubscriber objects.
    }
}
