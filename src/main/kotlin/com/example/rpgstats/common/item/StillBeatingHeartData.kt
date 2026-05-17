package com.example.rpgstats.common.item

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.item.ItemStack

object StillBeatingHeartData {
    const val DATA_TAG: String = "StillBeatingHeartData"
    private const val SCHEMA_VERSION: Int = 2

    @JvmStatic
    @Suppress("UNUSED_PARAMETER")
    fun create(player: ServerPlayer, source: DamageSource): ItemStack {
        val stack = ItemStack(ModItems.STILL_BEATING_HEART.get())
        val root = CompoundTag()

        root.putInt("schema_version", SCHEMA_VERSION)
        root.putInt("level", player.experienceLevel)

        stack.orCreateTag.put(DATA_TAG, root)
        return stack
    }

    @JvmStatic
    fun getData(stack: ItemStack): CompoundTag? {
        val tag = stack.tag ?: return null
        if (!tag.contains(DATA_TAG, Tag.TAG_COMPOUND.toInt())) return null
        return tag.getCompound(DATA_TAG)
    }

    @JvmStatic
    fun getLevel(stack: ItemStack): Int {
        val data = getData(stack) ?: return 0
        if (data.contains("level", Tag.TAG_INT.toInt())) return data.getInt("level")

        val legacyPlayer = data.getCompound("player")
        return legacyPlayer.getInt("experience_level")
    }
}
