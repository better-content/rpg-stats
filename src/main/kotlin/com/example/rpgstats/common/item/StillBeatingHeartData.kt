package com.example.rpgstats.common.item

import com.example.rpgstats.common.config.json.AttributeEffect
import com.example.rpgstats.common.curve.Curves
import com.example.rpgstats.common.data.StatsCap
import com.example.rpgstats.common.reload.RegistryState
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

object StillBeatingHeartData {
    const val DATA_TAG: String = "StillBeatingHeartData"
    private const val SCHEMA_VERSION: Int = 1

    @JvmStatic
    fun create(player: ServerPlayer, source: DamageSource): ItemStack {
        val stack = ItemStack(ModItems.STILL_BEATING_HEART.get())
        val root = CompoundTag()

        root.putInt("schema_version", SCHEMA_VERSION)
        root.putLong("captured_at_unix_ms", System.currentTimeMillis())
        root.put("player", playerTag(player))
        root.put("death", deathTag(player, source))
        root.put("location", locationTag(player))
        root.put("vitals", vitalsTag(player))
        root.put("rpgstats", rpgStatsTag(player))
        root.put("attributes", attributesTag(player))
        root.put("equipment", equipmentTag(player))

        stack.orCreateTag.put(DATA_TAG, root)
        return stack
    }

    @JvmStatic
    fun getData(stack: ItemStack): CompoundTag? {
        val tag = stack.tag ?: return null
        if (!tag.contains(DATA_TAG, Tag.TAG_COMPOUND.toInt())) return null
        return tag.getCompound(DATA_TAG)
    }

    private fun playerTag(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        tag.putUUID("uuid", player.uuid)
        tag.putString("name", player.gameProfile.name)
        tag.putString("display_name", player.displayName.string)
        tag.putString("scoreboard_name", player.scoreboardName)
        tag.putInt("experience_level", player.experienceLevel)
        tag.putFloat("experience_progress", player.experienceProgress)
        tag.putInt("total_experience", player.totalExperience)
        tag.putInt("score", player.score)
        return tag
    }

    private fun deathTag(player: ServerPlayer, source: DamageSource): CompoundTag {
        val tag = CompoundTag()
        tag.putString("message", player.combatTracker.deathMessage.string)
        tag.putString("cause_id", source.msgId)
        putEntity(tag, "attacker", source.entity)
        putEntity(tag, "direct_entity", source.directEntity)
        tag.putFloat("fall_distance", player.fallDistance)
        return tag
    }

    private fun locationTag(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        tag.putString("dimension", player.level().dimension().location().toString())
        tag.putLong("game_time", player.serverLevel().gameTime)
        tag.putLong("day_time", player.serverLevel().dayTime)
        tag.putDouble("x", player.x)
        tag.putDouble("y", player.y)
        tag.putDouble("z", player.z)
        tag.putInt("block_x", player.blockX)
        tag.putInt("block_y", player.blockY)
        tag.putInt("block_z", player.blockZ)
        tag.putFloat("yaw", player.yRot)
        tag.putFloat("pitch", player.xRot)
        return tag
    }

    private fun vitalsTag(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        tag.putFloat("health", player.health)
        tag.putFloat("max_health", player.maxHealth)
        tag.putFloat("absorption", player.absorptionAmount)
        tag.putInt("food", player.foodData.foodLevel)
        tag.putFloat("saturation", player.foodData.saturationLevel)
        tag.putInt("air", player.airSupply)
        tag.putInt("max_air", player.maxAirSupply)
        tag.putInt("remaining_fire_ticks", player.remainingFireTicks)
        return tag
    }

    private fun rpgStatsTag(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        val stats = StatsCap.get(player)
        val defs = RegistryState.snapshot().values.sortedBy { it.id.toString() }
        val entries = ListTag()

        if (stats != null) {
            tag.put("raw", stats.serializeNBT())
            tag.putInt("life_peak_level", stats.lifePeakLevel)
            tag.putInt("unspent_points", stats.unspentPoints)
            tag.putInt("total_allocated_points", stats.totalAllocated())
            tag.putInt("total_points_this_life", stats.totalPointsThisLife())
        }

        defs.forEach { def ->
            val points = stats?.allocations?.get(def.id.toString()) ?: 0
            val entry = CompoundTag()
            entry.putString("id", def.id.toString())
            entry.putString("name_key", def.nameKey)
            entry.putInt("points", points)
            entry.putInt("max_points", def.maxPoints)

            val effects = ListTag()
            def.effects.forEach { effect ->
                if (effect is AttributeEffect) {
                    val effectTag = CompoundTag()
                    effectTag.putString("attribute", effect.attributeId.toString())
                    effectTag.putString("operation", effect.operation.name)
                    effectTag.putDouble("value", Curves.eval(points, effect.curve))
                    effectTag.putBoolean("is_primary", effect.isPrimary)
                    effects.add(effectTag)
                }
            }
            entry.put("effects", effects)
            entries.add(entry)
        }

        tag.put("entries", entries)
        return tag
    }

    private fun attributesTag(player: ServerPlayer): ListTag {
        val entries = mutableListOf<CompoundTag>()

        for (attribute in ForgeRegistries.ATTRIBUTES.values) {
            val key = ForgeRegistries.ATTRIBUTES.getKey(attribute) ?: continue
            val instance = player.getAttribute(attribute) ?: continue
            entries += CompoundTag().apply {
                putString("id", key.toString())
                putString("name_key", attribute.getDescriptionId())
                putDouble("base", instance.baseValue)
                putDouble("value", instance.value)
            }
        }

        entries.sortBy { it.getString("id") }

        val list = ListTag()
        entries.forEach(list::add)
        return list
    }

    private fun equipmentTag(player: ServerPlayer): CompoundTag {
        val tag = CompoundTag()
        putStack(tag, "mainhand", player.mainHandItem)
        putStack(tag, "offhand", player.offhandItem)
        putStack(tag, "head", player.getItemBySlot(EquipmentSlot.HEAD))
        putStack(tag, "chest", player.getItemBySlot(EquipmentSlot.CHEST))
        putStack(tag, "legs", player.getItemBySlot(EquipmentSlot.LEGS))
        putStack(tag, "feet", player.getItemBySlot(EquipmentSlot.FEET))
        return tag
    }

    private fun putEntity(parent: CompoundTag, key: String, entity: Entity?) {
        if (entity == null) return

        val entityTag = CompoundTag()
        entityTag.putUUID("uuid", entity.uuid)
        entityTag.putString("name", entity.displayName.string)
        ForgeRegistries.ENTITY_TYPES.getKey(entity.type)?.let { entityTag.putString("type", it.toString()) }
        entityTag.putDouble("x", entity.x)
        entityTag.putDouble("y", entity.y)
        entityTag.putDouble("z", entity.z)
        parent.put(key, entityTag)
    }

    private fun putStack(parent: CompoundTag, key: String, stack: ItemStack) {
        if (stack.isEmpty) return

        val itemTag = CompoundTag()
        itemTag.putString("item", ForgeRegistries.ITEMS.getKey(stack.item)?.toString() ?: "minecraft:air")
        itemTag.putInt("count", stack.count)
        itemTag.putString("hover_name", stack.hoverName.string)
        itemTag.put("stack", stack.save(CompoundTag()))
        parent.put(key, itemTag)
    }
}
