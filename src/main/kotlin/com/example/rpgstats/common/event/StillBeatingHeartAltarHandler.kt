package com.example.rpgstats.common.event

import com.example.rpgstats.common.item.ModItems
import com.example.rpgstats.common.item.StillBeatingHeartData
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fml.ModList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

object StillBeatingHeartAltarHandler {
    private const val BLOODMAGIC_MODID = "bloodmagic"
    private const val BLOOD_ALTAR_CLASS = "wayoftime.bloodmagic.common.tile.TileAltar"
    private const val DISCOVERY_RADIUS = 8
    private const val DISCOVERY_INTERVAL_TICKS = 20
    private const val BASE_LP_PER_TICK = 5
    private const val LEVELS_PER_DOUBLING = 10.0
    private const val MAX_LP_PER_TICK = 4096

    private val trackedAltars = mutableMapOf<String, MutableSet<Long>>()
    private var fillMainTankMethod: java.lang.reflect.Method? = null

    fun isBloodMagicLoaded(): Boolean = ModList.get().isLoaded(BLOODMAGIC_MODID)

    fun discoverNear(player: net.minecraft.server.level.ServerPlayer) {
        if (!isBloodMagicLoaded()) return
        if (player.tickCount % DISCOVERY_INTERVAL_TICKS != 0) return

        val level = player.serverLevel()
        val origin = player.blockPosition()
        val dimensionKey = level.dimension().location().toString()
        val positions = trackedAltars.getOrPut(dimensionKey) { mutableSetOf() }

        BlockPos.betweenClosed(
            origin.offset(-DISCOVERY_RADIUS, -DISCOVERY_RADIUS, -DISCOVERY_RADIUS),
            origin.offset(DISCOVERY_RADIUS, DISCOVERY_RADIUS, DISCOVERY_RADIUS)
        ).forEach { pos ->
            val blockEntity = level.getBlockEntity(pos) ?: return@forEach
            if (isBloodAltar(blockEntity) && heartIn(blockEntity) != null) {
                positions += pos.asLong()
            }
        }
    }

    fun tickLevel(level: ServerLevel) {
        if (!isBloodMagicLoaded()) return

        val dimensionKey = level.dimension().location().toString()
        val positions = trackedAltars[dimensionKey] ?: return
        val iterator = positions.iterator()

        while (iterator.hasNext()) {
            val pos = BlockPos.of(iterator.next())
            val blockEntity = level.getBlockEntity(pos)
            if (blockEntity == null || !isBloodAltar(blockEntity)) {
                iterator.remove()
                continue
            }

            val heart = heartIn(blockEntity)
            if (heart == null) {
                iterator.remove()
                continue
            }

            val inserted = fillAltar(blockEntity, lpPerTick(StillBeatingHeartData.getLevel(heart)))
            if (inserted > 0) {
                blockEntity.setChanged()
            }
        }
    }

    fun lpPerTick(level: Int): Int {
        val scaled = BASE_LP_PER_TICK * 2.0.pow(max(0, level) / LEVELS_PER_DOUBLING)
        return min(MAX_LP_PER_TICK, max(1, scaled.toInt()))
    }

    private fun heartIn(blockEntity: Any): ItemStack? {
        val container = blockEntity as? Container ?: return null
        if (container.containerSize <= 0) return null

        val stack = container.getItem(0)
        if (stack.`is`(ModItems.STILL_BEATING_HEART.get())) return stack
        return null
    }

    private fun isBloodAltar(blockEntity: Any): Boolean {
        return blockEntity.javaClass.name == BLOOD_ALTAR_CLASS
    }

    private fun fillAltar(blockEntity: Any, amount: Int): Int {
        val method = fillMainTankMethod ?: blockEntity.javaClass.getMethod("fillMainTank", Int::class.javaPrimitiveType)
            .also { fillMainTankMethod = it }
        return method.invoke(blockEntity, amount) as? Int ?: 0
    }
}
