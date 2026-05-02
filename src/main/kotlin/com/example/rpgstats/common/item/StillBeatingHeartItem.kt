package com.example.rpgstats.common.item

import com.example.rpgstats.common.heart.HeartTypeRegistry
import net.minecraft.ChatFormatting
import net.minecraft.nbt.Tag
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
import java.text.DecimalFormat

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
        val data = StillBeatingHeartData.getData(stack) ?: return
        val player = data.getCompound("player")
        val death = data.getCompound("death")
        val location = data.getCompound("location")
        val vitals = data.getCompound("vitals")
        val rpgStats = data.getCompound("rpgstats")
        val ritual = data.getCompound("ritual")
        val statEntries = rpgStats.getList("entries", Tag.TAG_COMPOUND.toInt())
        val attributes = data.getList("attributes", Tag.TAG_COMPOUND.toInt())
        val equipment = data.getCompound("equipment")
        val deathMessage = shorten(death.getString("message"), 64)

        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.player", player.getString("name"))
            .withStyle(ChatFormatting.GRAY)
        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.killed_by", deathMessage)
            .withStyle(ChatFormatting.RED)
        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.location",
            shortDimension(location.getString("dimension")),
            location.getInt("block_x"),
            location.getInt("block_y"),
            location.getInt("block_z")
        ).withStyle(ChatFormatting.DARK_AQUA)
        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.level",
            player.getInt("experience_level"),
            rpgStats.getInt("life_peak_level")
        ).withStyle(ChatFormatting.BLUE)
        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.total_xp",
            player.getInt("total_experience")
        ).withStyle(ChatFormatting.BLUE)

        if (ritual.getBoolean("performed")) {
            tooltip += Component.translatable(
                "item.rpgstats.still_beating_heart.tooltip.ritual",
                ritual.getInt("tier"),
                ritual.getInt("stat_bonus_percent")
            ).withStyle(ChatFormatting.DARK_PURPLE)
        }

        if (!flag.isAdvanced) {
            tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.more")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
            return
        }

        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.vitals",
            number(vitals.getFloat("health").toDouble()),
            number(vitals.getFloat("max_health").toDouble()),
            vitals.getInt("food"),
            number(vitals.getFloat("absorption").toDouble())
        ).withStyle(ChatFormatting.DARK_RED)

        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.stats_header")
            .withStyle(ChatFormatting.GOLD)
        for (i in 0 until statEntries.size) {
            val stat = statEntries.getCompound(i)
            tooltip += Component.translatable(
                "item.rpgstats.still_beating_heart.tooltip.stat_line",
                Component.translatable(stat.getString("name_key")),
                stat.getInt("points")
            ).withStyle(ChatFormatting.YELLOW)
        }

        tooltip += Component.translatable(
            "item.rpgstats.still_beating_heart.tooltip.capture_summary",
            attributes.size,
            equipment.allKeys.size
        ).withStyle(ChatFormatting.DARK_GRAY)

        tooltip += Component.translatable("item.rpgstats.still_beating_heart.tooltip.attributes_header")
            .withStyle(ChatFormatting.DARK_GREEN)
        for (i in 0 until attributes.size) {
            val attribute = attributes.getCompound(i)
            tooltip += Component.translatable(
                "item.rpgstats.still_beating_heart.tooltip.attribute_line",
                Component.translatable(attribute.getString("name_key")),
                number(attribute.getDouble("value"))
            ).withStyle(ChatFormatting.GREEN)
        }
    }

    private fun shortDimension(dimension: String): String {
        return dimension.removePrefix("minecraft:")
    }

    private fun shorten(input: String, maxChars: Int): String {
        if (maxChars <= 3 || input.length <= maxChars) return input
        return input.take(maxChars - 3).trimEnd() + "..."
    }

    private fun number(value: Double): String = NUMBER_FORMAT.format(value)

    companion object {
        private val NUMBER_FORMAT = DecimalFormat("0.##")
    }
}
