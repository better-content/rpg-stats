package com.bettercontent.rpgstats.harness

import com.bettercontent.rpgstats.client.cache.ClientCache
import com.bettercontent.rpgstats.client.cache.ClientCurveDef
import com.bettercontent.rpgstats.client.cache.ClientEffectDef
import com.bettercontent.rpgstats.client.cache.ClientStatDef
import com.bettercontent.rpgstats.client.cache.ClientStatsSnapshot
import com.bettercontent.rpgstats.client.ui.StatsScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.Screenshot
import net.minecraft.client.gui.components.Button
import net.minecraftforge.event.TickEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent

/**
 * Development-only visual fixture. The dedicated runVisualHarness Gradle run includes this
 * source set alongside RPG Stats and captures the stats screen without a modpack or world.
 */
object RpgStatsVisualHarness {
    private val heartCaptureTicks = setOf(1, 13, 25, 37)
    private var opened = false
    private var startupTicks = 0
    private var screenTicks = 0
    private var statsCaptured = false
    private var heartScreenTicks = 0
    private var heartCaptures = 0
    private var ticksAfterCapture = 0

    @JvmStatic
    fun install() {
        MinecraftForge.EVENT_BUS.register(this)
        println("RPG_STATS_VISUAL_HARNESS installed")
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        val minecraft = Minecraft.getInstance()
        if (!opened) {
            if (minecraft.overlay == null && minecraft.screen != null) startupTicks++
            if (startupTicks >= 20) {
                seedFixture()
                val screen = StatsScreen()
                minecraft.setScreen(screen)
                screen.children().filterIsInstance<Button>().firstOrNull { it.message.string == "+" }?.onPress()
                opened = true
                println("RPG_STATS_VISUAL_HARNESS screen-ready")
            }
            return
        }

        if (!statsCaptured && minecraft.screen is StatsScreen) {
            screenTicks++
            if (screenTicks >= 40) {
                Screenshot.grab(minecraft.gameDirectory, minecraft.mainRenderTarget) { message ->
                    println("RPG_STATS_VISUAL_HARNESS stats-screenshot $message")
                }
                statsCaptured = true
                minecraft.setScreen(StillBeatingHeartAnimationScreen())
                println("RPG_STATS_VISUAL_HARNESS heart-screen-ready")
            }
            return
        }

        if (minecraft.screen is StillBeatingHeartAnimationScreen && heartCaptures < heartCaptureTicks.size) {
            heartScreenTicks++
            if (heartScreenTicks in heartCaptureTicks) {
                Screenshot.grab(minecraft.gameDirectory, minecraft.mainRenderTarget) { message ->
                    println("RPG_STATS_VISUAL_HARNESS heart-screenshot tick=$heartScreenTicks $message")
                }
                heartCaptures++
            }
            return
        }

        if (heartCaptures == heartCaptureTicks.size) {
            ticksAfterCapture++
            if (ticksAfterCapture >= 40) minecraft.stop()
        }
    }

    private fun seedFixture() {
        ClientCache.defs = listOf(
            stat("attack_damage", "⚔", 0xE35B52, effect("minecraft:generic.attack_damage", 0, 0.25)),
            stat("attack_speed", "⏱", 0xF0A84A, effect("minecraft:generic.attack_speed", 1, 0.025)),
            stat("mining_speed", "⛏", 0xE2C14F, effect("rpg_stats:mining_speed", 1, 0.045)),
            stat("movement_speed", "➜", 0x66C56C, effect("minecraft:generic.movement_speed", 1, 0.02)),
            stat(
                "temperature_resistance",
                "❄",
                0x55BCE8,
                resistanceEffect("cold_sweat:heat_resistance"),
                resistanceEffect("cold_sweat:cold_resistance", primary = false)
            ),
            stat("hunger_efficiency", "◆", 0xB779E8, effect("rpg_stats:hunger_efficiency", 1, 0.03)),
            stat("thirst_efficiency", "●", 0x4E92E8, effect("rpg_stats:thirst_efficiency", 1, 0.03))
        )
        ClientCache.stats = ClientStatsSnapshot(
            unspent = 4,
            lifePeak = 27,
            allocations = mapOf(
                "rpg_stats:attack_damage" to 4,
                "rpg_stats:attack_speed" to 2,
                "rpg_stats:mining_speed" to 3,
                "rpg_stats:movement_speed" to 1,
                "rpg_stats:temperature_resistance" to 2
            )
        )
    }

    private fun stat(id: String, icon: String, color: Int, vararg effects: ClientEffectDef): ClientStatDef =
        ClientStatDef(
            id = "rpg_stats:$id",
            nameKey = "stat.rpg_stats.$id",
            maxPoints = -1,
            effects = effects.toList(),
            icon = icon,
            color = color
        )

    private fun effect(attributeId: String, operation: Int, perPoint: Double, primary: Boolean = true) =
        ClientEffectDef(
            attributeId = attributeId,
            operation = operation,
            curve = ClientCurveDef(
                type = "linear",
                cap = Double.POSITIVE_INFINITY,
                k = 0.0,
                perPoint = perPoint,
                min = Double.NEGATIVE_INFINITY,
                max = Double.POSITIVE_INFINITY
            ),
            isPrimary = primary
        )

    private fun resistanceEffect(attributeId: String, primary: Boolean = true) =
        ClientEffectDef(
            attributeId = attributeId,
            operation = 0,
            curve = ClientCurveDef(
                type = "hyperbola",
                cap = 1.0,
                k = 10.0,
                perPoint = 0.0,
                min = 0.0,
                max = 1.0
            ),
            isPrimary = primary
        )
}
