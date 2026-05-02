package com.example.rpgstats.common.item

import com.example.rpgstats.RpgStatsMod
import com.example.rpgstats.common.heart.TypedHeartItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModItems {
    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, RpgStatsMod.MODID)

    val STILL_BEATING_HEART: RegistryObject<Item> = ITEMS.register("still_beating_heart") {
        StillBeatingHeartItem(
            Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.UNCOMMON),
            "item.rpgstats.still_beating_heart.named"
        )
    }

    val HEART_FLESH: RegistryObject<Item> = ITEMS.register("heart_flesh") {
        StillBeatingHeartItem(
            Item.Properties()
                .stacksTo(64),
            "item.rpgstats.heart_flesh.named"
        )
    }

    val BONE_RITUAL_DAGGER: RegistryObject<Item> = ITEMS.register("bone_ritual_dagger") {
        RitualDaggerItem(Item.Properties().stacksTo(1).durability(12), tier = 1, minStatBonusPercent = 5, maxStatBonusPercent = 15, channelTicks = 60)
    }

    val IRON_RITUAL_DAGGER: RegistryObject<Item> = ITEMS.register("iron_ritual_dagger") {
        RitualDaggerItem(Item.Properties().stacksTo(1).durability(28), tier = 2, minStatBonusPercent = 10, maxStatBonusPercent = 25, channelTicks = 80)
    }

    val GOLD_RITUAL_DAGGER: RegistryObject<Item> = ITEMS.register("gold_ritual_dagger") {
        RitualDaggerItem(Item.Properties().stacksTo(1).durability(18), tier = 3, minStatBonusPercent = 18, maxStatBonusPercent = 40, channelTicks = 90)
    }

    val DIAMOND_RITUAL_DAGGER: RegistryObject<Item> = ITEMS.register("diamond_ritual_dagger") {
        RitualDaggerItem(Item.Properties().stacksTo(1).durability(48).rarity(Rarity.UNCOMMON), tier = 4, minStatBonusPercent = 30, maxStatBonusPercent = 65, channelTicks = 100)
    }

    val ECHO_RITUAL_DAGGER: RegistryObject<Item> = ITEMS.register("echo_ritual_dagger") {
        RitualDaggerItem(Item.Properties().stacksTo(1).durability(64).rarity(Rarity.RARE), tier = 5, minStatBonusPercent = 50, maxStatBonusPercent = 100, channelTicks = 120)
    }

    val MYOFIBRA_CATALYST: RegistryObject<Item> = registerCatalyst("myofibra_catalyst", "stat.rpgstats.myofibra")
    val SYNAPSIS_CATALYST: RegistryObject<Item> = registerCatalyst("synapsis_catalyst", "stat.rpgstats.synapsis")
    val OSTEON_CATALYST: RegistryObject<Item> = registerCatalyst("osteon_catalyst", "stat.rpgstats.osteon")
    val HEMOSTASIS_CATALYST: RegistryObject<Item> = registerCatalyst("hemostasis_catalyst", "stat.rpgstats.hemostasis")
    val CARPUS_CATALYST: RegistryObject<Item> = registerCatalyst("carpus_catalyst", "stat.rpgstats.carpus")

    val MYOFIBRA_HEART: RegistryObject<Item> = registerTypedHeart("myofibra_heart")
    val SYNAPSIS_HEART: RegistryObject<Item> = registerTypedHeart("synapsis_heart")
    val OSTEON_HEART: RegistryObject<Item> = registerTypedHeart("osteon_heart")
    val HEMOSTASIS_HEART: RegistryObject<Item> = registerTypedHeart("hemostasis_heart")
    val CARPUS_HEART: RegistryObject<Item> = registerTypedHeart("carpus_heart")

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }

    private fun registerCatalyst(name: String, statTranslationKey: String): RegistryObject<Item> {
        return ITEMS.register(name) {
            HeartCatalystItem(Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON), statTranslationKey)
        }
    }

    private fun registerTypedHeart(name: String): RegistryObject<Item> {
        return ITEMS.register(name) {
            TypedHeartItem()
        }
    }
}
