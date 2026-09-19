package com.bettercontent.rpgstats.common.attribute

import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile

/** Identifies player-owned combat without treating owned creature or environmental damage as player power. */
object OutgoingDamageScaling {
    fun playerFor(source: DamageSource): Player? = when (val attacker = source.entity) {
        is Player -> attacker
        else -> (source.directEntity as? Projectile)?.owner as? Player
    }

    fun multiplier(player: Player): Float = multiplier(player.getAttributeValue(ModAttributes.OUTGOING_DAMAGE.get()))

    fun multiplier(progress: Double): Float = (1.0 + progress).coerceAtLeast(0.0).toFloat()

    fun scale(source: DamageSource, target: LivingEntity, amount: Float): Float {
        if (target.level().isClientSide || amount <= 0.0f) return amount
        val player = playerFor(source) ?: return amount
        return scaleAmount(amount, multiplier(player))
    }

    fun scaleAmount(amount: Float, multiplier: Float): Float = amount * multiplier
}
