package com.example.rpgstats.common.item

import com.example.rpgstats.RpgStatsMod
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

    fun register(bus: IEventBus) {
        ITEMS.register(bus)
    }
}
