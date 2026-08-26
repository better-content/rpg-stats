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
    private var soundScreenTicks = 0
    private var soundCaptured = false
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
                minecraft.setScreen(IdentitySoundReviewScreen())
                println("RPG_STATS_VISUAL_HARNESS sound-review-ready")
            }
            return
        }

        if (!soundCaptured && minecraft.screen is IdentitySoundReviewScreen) {
            soundScreenTicks++
            if (soundScreenTicks % 12 == 1) {
                val index = soundScreenTicks / 12
                com.bettercontent.rpgstats.common.salience.AspectIdentity.entries.getOrNull(index)?.let {
                    (minecraft.screen as IdentitySoundReviewScreen).play(it)
                }
            }
            if (soundScreenTicks >= 100) {
                Screenshot.grab(minecraft.gameDirectory, minecraft.mainRenderTarget) { message ->
                    println("RPG_STATS_VISUAL_HARNESS sound-review-screenshot $message")
                }
                soundCaptured = true
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
            stat(
                "impact", "✦", 0xE4717D,
                effect("minecraft:generic.attack_damage", 0, 6.0),
                effect("epicfight:impact", 0, 0.75, primary = false),
                effect("minecraft:generic.attack_knockback", 0, 0.4, primary = false)
            ),
            stat(
                "tempo", "»", 0xAA652B,
                effect("minecraft:generic.attack_speed", 0, 0.6),
                effect("tconstruct:player.use_item_speed", 1, 0.25, primary = false)
            ),
            stat("work", "⚒", 0xCAA903, effect("rpg_stats:mining_speed", 1, 0.75), effect("forge:block_reach", 0, 1.0, primary = false)),
            stat("mobility", "➜", 0xC0E304, effect("minecraft:generic.movement_speed", 0, 0.05), effect("forge:swim_speed", 1, 0.25, primary = false)),
            stat(
                "endurance", "∞", 0x35BBD0,
                effect("rpg_stats:hunger_efficiency", 1, 1.0),
                effect("rpg_stats:thirst_efficiency", 1, 1.0, primary = false),
                effect("epicfight:staminar", 1, 0.3, primary = false)
            ),
            stat(
                "robustness", "◆", 0x1175FC,
                effect("cold_sweat:heat_resistance", 0, 0.6, displayAsPercent = true),
                effect("cold_sweat:cold_resistance", 0, 0.6, primary = false, displayAsPercent = true)
            ),
            stat(
                "renewal", "✚", 0x6FEDBA,
                effect("rpg_stats:harmful_effect_duration_reduction", 0, 0.25, displayAsPercent = true),
                effect("rpg_stats:beneficial_effect_duration", 0, 0.25, primary = false, displayAsPercent = true)
            ),
            stat(
                "control", "⊕", 0x8A6CB2,
                effect("rpg_stats:recoil_reduction", 0, 0.35, displayAsPercent = true),
                effect("rpg_stats:dispersion_reduction", 0, 0.35, primary = false, displayAsPercent = true),
                effect("goety:spell_range", 1, 0.25, primary = false)
            )
        )
        ClientCache.stats = ClientStatsSnapshot(
            unspent = 4,
            lifePeak = 27,
            allocations = mapOf(
                "rpg_stats:impact" to 4,
                "rpg_stats:tempo" to 2,
                "rpg_stats:work" to 3,
                "rpg_stats:mobility" to 1,
                "rpg_stats:endurance" to 2,
                "rpg_stats:robustness" to 2,
                "rpg_stats:renewal" to 2,
                "rpg_stats:control" to 1
            )
        )
    }

    private fun stat(id: String, icon: String, color: Int, vararg effects: ClientEffectDef): ClientStatDef =
        ClientStatDef(
            id = "rpg_stats:$id",
            nameKey = "stat.rpg_stats.${visibleNames.getValue(id)}",
            maxPoints = -1,
            effects = effects.toList(),
            icon = icon,
            color = color
        )

    private val visibleNames = mapOf(
        "impact" to "strength",
        "tempo" to "dexterity",
        "work" to "aptitude",
        "mobility" to "agility",
        "endurance" to "constitution",
        "robustness" to "fortitude",
        "renewal" to "vitality",
        "control" to "perception"
    )

    private fun effect(
        attributeId: String,
        operation: Int,
        cap: Double,
        primary: Boolean = true,
        displayAsPercent: Boolean = operation != 0
    ) =
        ClientEffectDef(
            attributeId = attributeId,
            operation = operation,
            curve = ClientCurveDef(
                type = "hyperbola",
                cap = cap,
                k = 20.0,
                perPoint = 0.0,
                min = 0.0,
                max = cap
            ),
            isPrimary = primary,
            displayAsPercent = displayAsPercent
        )

}
