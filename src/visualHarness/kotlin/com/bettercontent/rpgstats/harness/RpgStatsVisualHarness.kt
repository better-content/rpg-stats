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
    private var opened = false
    private var startupTicks = 0
    private var screenTicks = 0
    private var captured = false
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

        if (!captured && minecraft.screen is StatsScreen) {
            screenTicks++
            if (screenTicks >= 40) {
                Screenshot.grab(minecraft.gameDirectory, minecraft.mainRenderTarget) { message ->
                    println("RPG_STATS_VISUAL_HARNESS screenshot $message")
                }
                captured = true
            }
            return
        }

        if (captured) {
            ticksAfterCapture++
            if (ticksAfterCapture >= 40) minecraft.stop()
        }
    }

    private fun seedFixture() {
        ClientCache.defs = listOf(
            stat("attack_damage", "⚔", 0xE35B52, effect("minecraft:generic.attack_damage", 0, 0.25)),
            stat("attack_speed", "⏱", 0xF0A84A, effect("minecraft:generic.attack_speed", 1, 0.025)),
            stat("mining_speed", "⛏", 0xE2C14F, effect("attributeslib:mining_speed", 1, 0.04)),
            stat("movement_speed", "➜", 0x66C56C, effect("minecraft:generic.movement_speed", 1, 0.02)),
            stat(
                "temperature_resistance",
                "❄",
                0x55BCE8,
                effect("cold_sweat:heat_resistance", 1, 0.035),
                effect("cold_sweat:cold_resistance", 1, 0.035, primary = false)
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
            maxPoints = 10,
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
}
