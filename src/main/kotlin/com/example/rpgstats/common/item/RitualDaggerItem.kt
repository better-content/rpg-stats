package com.example.rpgstats.common.item

import com.example.rpgstats.common.ritual.RitualConstants
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class RitualDaggerItem(
    properties: Properties,
    private val tier: Int,
    private val minStatBonusPercent: Int,
    private val maxStatBonusPercent: Int,
    private val channelTicks: Int
) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack)
        if (player.isCreative || player.isSpectator) return InteractionResultHolder.pass(stack)

        player.startUsingItem(hand)
        return InteractionResultHolder.consume(stack)
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.BOW

    override fun getUseDuration(stack: ItemStack): Int = MAX_USE_DURATION

    override fun onUseTick(level: Level, living: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (level.isClientSide) return
        val player = living as? ServerPlayer ?: return
        val serverLevel = level as? ServerLevel ?: return

        val chargedTicks = getUseDuration(stack) - remainingUseTicks
        if (chargedTicks <= 0 || chargedTicks > channelTicks) return

        val progress = chargedTicks.toDouble() / channelTicks.toDouble()
        if (chargedTicks % 4 == 0) {
            spawnChannelParticles(serverLevel, player, progress)
        }
        if (chargedTicks % 20 == 0 || chargedTicks == channelTicks) {
            playHeartbeat(serverLevel, player, progress)
        }
        if (chargedTicks >= channelTicks) {
            performRitual(serverLevel, player, stack)
        }
    }

    override fun releaseUsing(stack: ItemStack, level: Level, living: LivingEntity, timeLeft: Int) {
        if (level.isClientSide) return
        val player = living as? ServerPlayer ?: return
        if (player.isCreative || player.isSpectator) return

        val chargedTicks = getUseDuration(stack) - timeLeft
        if (chargedTicks < channelTicks) return

        performRitual(level, player, stack)
    }

    private fun performRitual(level: Level, player: ServerPlayer, stack: ItemStack) {
        val rolledBonusPercent = player.random.nextIntBetweenInclusive(minStatBonusPercent, maxStatBonusPercent)
        val data = player.persistentData
        data.putInt(RitualConstants.PENDING_TIER_TAG, tier)
        data.putInt(RitualConstants.PENDING_BONUS_PERCENT_TAG, rolledBonusPercent)
        data.putString(RitualConstants.PENDING_DAGGER_TAG, stack.item.descriptionId)

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.WITHER_SPAWN,
            SoundSource.PLAYERS,
            0.4f,
            1.8f
        )

        player.hurt(player.damageSources().magic(), Float.MAX_VALUE)
        if (!player.isDeadOrDying) {
            data.remove(RitualConstants.PENDING_TIER_TAG)
            data.remove(RitualConstants.PENDING_BONUS_PERCENT_TAG)
            data.remove(RitualConstants.PENDING_DAGGER_TAG)
        }
        player.stopUsingItem()
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip += Component.translatable("item.rpgstats.ritual_dagger.tooltip.charge", channelTicks / 20.0)
            .withStyle(ChatFormatting.GRAY)
        tooltip += Component.translatable("item.rpgstats.ritual_dagger.tooltip.bonus_range", minStatBonusPercent, maxStatBonusPercent)
            .withStyle(ChatFormatting.DARK_RED)
    }

    private fun spawnChannelParticles(level: ServerLevel, player: ServerPlayer, progress: Double) {
        val random = player.random
        val count = 1 + (progress * 3.0).toInt()
        repeat(count) {
            val angle = random.nextDouble() * Math.PI * 2.0
            val radius = 0.45 + random.nextDouble() * 0.45
            val x = player.x + kotlin.math.cos(angle) * radius
            val y = player.y + 0.15 + random.nextDouble() * 1.25
            val z = player.z + kotlin.math.sin(angle) * radius
            level.sendParticles(
                ParticleTypes.SCULK_SOUL,
                x,
                y,
                z,
                1,
                0.015,
                0.08 + progress * 0.08,
                0.015,
                0.01 + progress * 0.015
            )
        }
    }

    private fun playHeartbeat(level: ServerLevel, player: ServerPlayer, progress: Double) {
        val volume = (0.05f + progress.toFloat() * 0.85f).coerceIn(0.05f, 0.9f)
        val pitch = (0.65f + progress.toFloat() * 0.25f).coerceIn(0.65f, 0.9f)
        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.WARDEN_HEARTBEAT,
            SoundSource.PLAYERS,
            volume,
            pitch
        )
    }

    companion object {
        private const val MAX_USE_DURATION = 72000
    }
}
