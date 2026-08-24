package com.bettercontent.rpgstats.common.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class StillBeatingHeartItem(properties: Properties) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag
    ) {
        if (StillBeatingHeartData.getData(stack) == null) return

        val level = StillBeatingHeartData.getLevel(stack)
        tooltip += Component.translatable("item.rpg_stats.still_beating_heart.tooltip.level", level)
        tooltip += Component.translatable(
            "item.rpg_stats.still_beating_heart.tooltip.altar_rate",
            StillBeatingHeartData.lpPerTick(level)
        )
    }

}
