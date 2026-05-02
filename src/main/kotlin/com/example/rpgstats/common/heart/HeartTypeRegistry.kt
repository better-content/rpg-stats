package com.example.rpgstats.common.heart

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.registries.ForgeRegistries
import org.apache.logging.log4j.LogManager

object HeartTypeRegistry {
    private val LOGGER = LogManager.getLogger("RPGStats/HeartTypes")

    @Volatile
    private var defs: List<HeartTypeDefinition> = emptyList()

    fun replaceFromKubeJs(newDefs: List<HeartTypeDefinition>) {
        val ids = mutableSetOf<ResourceLocation>()
        val itemIds = mutableSetOf<ResourceLocation>()
        val clean = mutableListOf<HeartTypeDefinition>()

        for (def in newDefs) {
            if (!ids.add(def.typeId)) {
                LOGGER.warn("Duplicate heart type id '{}'. Keeping first definition only.", def.typeId)
                continue
            }
            if (!itemIds.add(def.outputItemId)) {
                LOGGER.warn("Duplicate heart output item id '{}'. Keeping first definition only.", def.outputItemId)
                continue
            }
            clean += def.copy(channelTime = def.channelTime.coerceAtLeast(1))
        }

        defs = clean.sortedByDescending { it.priority }
        LOGGER.info("Loaded {} KubeJS heart type definitions.", defs.size)
    }

    fun definitions(): List<HeartTypeDefinition> = defs

    fun findMatch(data: CompoundTag, catalystStack: ItemStack): HeartTypeDefinition? {
        val matches = defs.filter { def ->
            catalystMatches(def, catalystStack) && def.requirements.matches(data)
        }
        if (matches.isEmpty()) return null
        if (matches.size == 1) return matches[0]

        val bestPriority = matches.maxOf { it.priority }
        val top = matches.filter { it.priority == bestPriority }
        if (top.size == 1) return top[0]

        LOGGER.warn(
            "Ambiguous heart conversion for catalyst '{}': {} candidates share priority {} ({})",
            catalystStack.item.descriptionId,
            top.size,
            bestPriority,
            top.joinToString(", ") { it.typeId.toString() }
        )
        return null
    }

    fun createResultStack(def: HeartTypeDefinition): ItemStack? {
        val item = ForgeRegistries.ITEMS.getValue(def.outputItemId)
        if (item == null || item == Items.AIR) {
            LOGGER.warn("Heart type '{}' output item '{}' is not registered.", def.typeId, def.outputItemId)
            return null
        }
        return ItemStack(item)
    }

    private fun catalystMatches(def: HeartTypeDefinition, stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        val raw = def.catalyst.trim()
        if (raw.isEmpty()) return false

        return if (raw.startsWith("#")) {
            val tagId = ResourceLocation.tryParse(raw.substring(1)) ?: return false
            val tagKey = TagKey.create(ForgeRegistries.ITEMS.registryKey, tagId)
            stack.`is`(tagKey)
        } else {
            val itemId = ResourceLocation.tryParse(raw) ?: return false
            val item: Item = ForgeRegistries.ITEMS.getValue(itemId) ?: return false
            stack.`is`(item)
        }
    }
}
