package com.bettercontent.rpgstats.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag

class PlayerStats {
    var lifePeakLevel: Int = 0
    var unspentPoints: Int = 0
    val allocations: MutableMap<String, Int> = mutableMapOf()

    fun totalAllocated(): Int = allocations.values.sum()

    fun totalPointsThisLife(): Int = unspentPoints + totalAllocated()

    fun serializeNBT(): CompoundTag {
        val tag = CompoundTag()
        tag.putInt("lifePeakLevel", lifePeakLevel)
        tag.putInt("unspentPoints", unspentPoints)

        val list = ListTag()
        allocations.forEach { (id, pts) ->
            val e = CompoundTag()
            e.putString("id", id)
            e.putInt("pts", pts)
            list.add(e)
        }
        tag.put("allocations", list)
        return tag
    }

    fun deserializeNBT(tag: CompoundTag) {
        lifePeakLevel = tag.getInt("lifePeakLevel")
        unspentPoints = tag.getInt("unspentPoints")
        allocations.clear()

        val list = tag.getList("allocations", Tag.TAG_COMPOUND.toInt())
        for (i in 0 until list.size) {
            val e = list.getCompound(i)
            val id = e.getString("id")
            val pts = e.getInt("pts")
            if (id.isNotBlank() && pts > 0) allocations[id] = pts
        }
    }
}
