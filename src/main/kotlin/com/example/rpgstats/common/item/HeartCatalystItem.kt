package com.example.rpgstats.common.item

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class HeartCatalystItem(
    properties: Properties,
    private val statTranslationKey: String
) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        tooltip += Component.translatable("item.rpgstats.heart_catalyst.tooltip", Component.translatable(statTranslationKey))
            .withStyle(ChatFormatting.GRAY)
    }
}
