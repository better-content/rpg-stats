package com.example.rpgstats.common.item

import com.example.rpgstats.common.heart.HeartTypeRegistry
import net.minecraft.network.chat.Component
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

class StillBeatingHeartItem(
    properties: Properties,
    private val namedTranslationKey: String
) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (hand != InteractionHand.MAIN_HAND) return InteractionResultHolder.pass(stack)

        val data = StillBeatingHeartData.getData(stack) ?: return InteractionResultHolder.pass(stack)
        val match = HeartTypeRegistry.findMatch(data, player.offhandItem) ?: return InteractionResultHolder.pass(stack)
        if (match.channelTime <= 0) return InteractionResultHolder.pass(stack)

        player.startUsingItem(hand)
        return InteractionResultHolder.consume(stack)
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.BOW

    override fun getUseDuration(stack: ItemStack): Int = 72000

    override fun onUseTick(level: Level, living: LivingEntity, stack: ItemStack, remainingUseTicks: Int) {
        if (level.isClientSide) return
        val player = living as? ServerPlayer ?: return

        val data = StillBeatingHeartData.getData(stack) ?: return
        val match = HeartTypeRegistry.findMatch(data, player.offhandItem) ?: return

        val usedTicks = getUseDuration(stack) - remainingUseTicks
        if (usedTicks < match.channelTime) return

        val result = HeartTypeRegistry.createResultStack(match) ?: return

        stack.shrink(1)
        if (match.consumeCatalyst) {
            player.offhandItem.shrink(1)
        }

        if (!player.inventory.add(result.copy())) {
            player.drop(result, false)
        }

        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.ENCHANTMENT_TABLE_USE,
            SoundSource.PLAYERS,
            0.65f,
            1.2f
        )
        player.stopUsingItem()
    }

    override fun getName(stack: ItemStack): Component {
        val data = StillBeatingHeartData.getData(stack) ?: return super.getName(stack)
        val player = data.getCompound("player").getString("name")
        if (player.isBlank()) return super.getName(stack)
        return Component.translatable(namedTranslationKey, player)
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        if (StillBeatingHeartData.getData(stack) == null) return

        val level = StillBeatingHeartData.getLevel(stack)
        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.description")
        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.level", level)
        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.altar_rate",
            StillBeatingHeartData.lpPerTick(level)
        )
        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.font")
    }

}
