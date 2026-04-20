package com.example.rpgstats

import com.example.rpgstats.common.item.ModItems
import com.example.rpgstats.common.network.Network
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.ModLoadingContext

@Mod(RpgStatsMod.MODID)
object RpgStatsMod {
    const val MODID: String = "rpgstats"

    init {
        ModItems.register(resolveModEventBus())
        Network.init()
        // Everything else is registered via @EventBusSubscriber objects.
    }

    private fun resolveModEventBus(): IEventBus {
        val loaderContext = ModLoadingContext.get().extension<Any>()
            ?: error("No active mod loading context available for $MODID")
        val accessor = loaderContext.javaClass.methods.firstOrNull {
            it.parameterCount == 0 && (it.name == "getKEventBus" || it.name == "getModEventBus")
        } ?: error("Unsupported mod loading context: ${loaderContext.javaClass.name}")

        return accessor.invoke(loaderContext) as? IEventBus
            ?: error("Mod event bus accessor returned an unexpected value for $MODID")
    }
}
