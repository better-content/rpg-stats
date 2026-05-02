package com.example.rpgstats.common.heart

import com.example.rpgstats.RpgStatsMod
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegisterEvent
import org.apache.logging.log4j.LogManager

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
object HeartTypeItems {
    private val LOGGER = LogManager.getLogger("RPGStats/HeartTypes")
    private val BUILTIN_HEARTS = setOf(
        "myofibra_heart",
        "synapsis_heart",
        "osteon_heart",
        "hemostasis_heart",
        "carpus_heart"
    )

    @SubscribeEvent
    fun onRegisterItems(event: RegisterEvent) {
        event.register(ForgeRegistries.Keys.ITEMS) { helper ->
            for (def in HeartTypeRegistry.definitions()) {
                if (def.outputItemId.namespace == RpgStatsMod.MODID && def.outputItemId.path in BUILTIN_HEARTS) {
                    continue
                }
                helper.register(def.outputItemId, TypedHeartItem())
                LOGGER.info("Registered typed heart item '{}'", def.outputItemId)
            }
        }
    }
}
