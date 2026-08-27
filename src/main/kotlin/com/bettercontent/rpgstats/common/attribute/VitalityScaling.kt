package com.bettercontent.rpgstats.common.attribute

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player

object VitalityScaling {
    @JvmStatic
    fun scale(entity: LivingEntity, original: MobEffectInstance): MobEffectInstance {
        val player = entity as? Player ?: return original
        val adjustment = if (original.effect.isBeneficial) {
            player.getAttributeValue(ModAttributes.BENEFICIAL_EFFECT_DURATION.get())
        } else {
            -player.getAttributeValue(ModAttributes.HARMFUL_EFFECT_DURATION_REDUCTION.get())
        }
        if (adjustment == 0.0 || original.duration <= 1) return original
        val duration = (original.duration * (1.0 + adjustment)).toInt().coerceAtLeast(1)
        return MobEffectInstance(
            original.effect,
            duration,
            original.amplifier,
            original.isAmbient,
            original.isVisible,
            original.isVisible
        )
    }
}
