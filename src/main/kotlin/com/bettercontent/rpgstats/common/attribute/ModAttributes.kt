package com.bettercontent.rpgstats.common.attribute

import com.bettercontent.rpgstats.RpgStatsMod
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.RangedAttribute
import net.minecraftforge.event.entity.EntityAttributeModificationEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModAttributes {
    private val ATTRIBUTES: DeferredRegister<Attribute> =
        DeferredRegister.create(ForgeRegistries.ATTRIBUTES, RpgStatsMod.MODID)

    val HUNGER_EFFICIENCY: RegistryObject<Attribute> = ATTRIBUTES.register("hunger_efficiency") {
        RangedAttribute("attribute.name.rpg_stats.hunger_efficiency", 1.0, 1.0, 1024.0).setSyncable(true)
    }

    val THIRST_EFFICIENCY: RegistryObject<Attribute> = ATTRIBUTES.register("thirst_efficiency") {
        RangedAttribute("attribute.name.rpg_stats.thirst_efficiency", 1.0, 1.0, 1024.0).setSyncable(true)
    }

    val MINING_SPEED: RegistryObject<Attribute> = ATTRIBUTES.register("mining_speed") {
        RangedAttribute("attribute.name.rpg_stats.mining_speed", 1.0, 1.0, 1024.0).setSyncable(true)
    }

    val RECOIL_REDUCTION: RegistryObject<Attribute> = ATTRIBUTES.register("recoil_reduction") {
        RangedAttribute("attribute.name.rpg_stats.recoil_reduction", 0.0, -1.0, 1.0).setSyncable(true)
    }

    val DISPERSION_REDUCTION: RegistryObject<Attribute> = ATTRIBUTES.register("dispersion_reduction") {
        RangedAttribute("attribute.name.rpg_stats.dispersion_reduction", 0.0, -1.0, 1.0).setSyncable(true)
    }

    val HARMFUL_EFFECT_DURATION_REDUCTION: RegistryObject<Attribute> = ATTRIBUTES.register("harmful_effect_duration_reduction") {
        RangedAttribute("attribute.name.rpg_stats.harmful_effect_duration_reduction", 0.0, 0.0, 0.25).setSyncable(true)
    }

    val BENEFICIAL_EFFECT_DURATION: RegistryObject<Attribute> = ATTRIBUTES.register("beneficial_effect_duration") {
        RangedAttribute("attribute.name.rpg_stats.beneficial_effect_duration", 0.0, 0.0, 0.25).setSyncable(true)
    }

    fun register(bus: IEventBus) {
        ATTRIBUTES.register(bus)
    }
}

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
object PlayerAttributeRegistration {
    @SubscribeEvent
    fun onEntityAttributeModification(event: EntityAttributeModificationEvent) {
        event.add(EntityType.PLAYER, ModAttributes.HUNGER_EFFICIENCY.get())
        event.add(EntityType.PLAYER, ModAttributes.THIRST_EFFICIENCY.get())
        event.add(EntityType.PLAYER, ModAttributes.MINING_SPEED.get())
        event.add(EntityType.PLAYER, ModAttributes.RECOIL_REDUCTION.get())
        event.add(EntityType.PLAYER, ModAttributes.DISPERSION_REDUCTION.get())
        event.add(EntityType.PLAYER, ModAttributes.HARMFUL_EFFECT_DURATION_REDUCTION.get())
        event.add(EntityType.PLAYER, ModAttributes.BENEFICIAL_EFFECT_DURATION.get())
    }
}
