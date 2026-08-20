package com.bettercontent.rpgstats.common.resource

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.io.path.name
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RpgStatsResourceTest {
    private val expectedStats = setOf(
        "attack_damage",
        "attack_speed",
        "hunger_efficiency",
        "mining_speed",
        "movement_speed",
        "temperature_resistance",
        "thirst_efficiency"
    )

    @Test
    fun `stat resources have matching names and valid attribute effects when present`() {
        val statsDir = Path.of("src/main/resources/data/rpg_stats/stats")
        val statFiles = Files.list(statsDir).use { paths ->
            paths.filter { it.name.endsWith(".json") }.sorted().toList()
        }

        assertEquals(expectedStats, statFiles.map { it.name.removeSuffix(".json") }.toSet())

        statFiles.forEach { path ->
            val id = path.fileName.toString().removeSuffix(".json")
            val json = Files.newBufferedReader(path).use { JsonParser.parseReader(it).asJsonObject }

            assertEquals("stat.rpg_stats.$id", json.string("name_key"))
            assertTrue(json.string("color").matches(Regex("#[0-9A-Fa-f]{6}")), "invalid color in $path")
            assertFalse(json.has("max_points"), "bundled stats must remain uncapped in $path")

            val effects = json.getAsJsonArray("effects")
            assertFalse(effects.isEmpty, "dead stat definition in $path")
            effects.forEach { element ->
                val effect = element.asJsonObject
                assertEquals("attribute", effect.string("type"), "unsupported effect type in $path")
                assertTrue(effect.string("attribute").contains(":"), "attribute must be namespaced in $path")
                assertTrue(effect.string("operation") in setOf("add", "multiply_base", "multiply_total"))
                val attribute = effect.string("attribute")
                assertFalse(attribute.contains("max_health"), "health scaling is forbidden in $path")
                assertFalse(attribute.contains("regeneration"), "regeneration scaling is forbidden in $path")
                assertFalse(attribute.contains("healing_received"), "healing scaling is forbidden in $path")
                validateCurve(effect.getAsJsonObject("curve"), path)
            }
        }
    }

    @Test
    fun `efficiency stats grant four percent per point`() {
        listOf("hunger_efficiency", "thirst_efficiency").forEach { id ->
            val path = Path.of("src/main/resources/data/rpg_stats/stats/$id.json")
            val json = Files.newBufferedReader(path).use { JsonParser.parseReader(it).asJsonObject }
            val effect = json.getAsJsonArray("effects").single().asJsonObject
            assertEquals("rpg_stats:$id", effect.string("attribute"))
            assertEquals("multiply_base", effect.string("operation"))
            assertEquals(0.04, effect.getAsJsonObject("curve").double("per_point"), 0.000001)
        }
    }

    @Test
    fun `mining speed uses the native synchronized multiplier`() {
        val json = statJson("mining_speed")
        val effect = json.getAsJsonArray("effects").single().asJsonObject

        assertEquals("rpg_stats:mining_speed", effect.string("attribute"))
        assertEquals("multiply_base", effect.string("operation"))
        assertEquals(0.045, effect.getAsJsonObject("curve").double("per_point"), 0.000001)
    }

    @Test
    fun `temperature resistance is symmetric and strongly diminishing`() {
        val effects = statJson("temperature_resistance").getAsJsonArray("effects").map { it.asJsonObject }

        assertEquals(setOf("cold_sweat:heat_resistance", "cold_sweat:cold_resistance"),
            effects.map { it.string("attribute") }.toSet())
        effects.forEach { effect ->
            val curve = effect.getAsJsonObject("curve")
            assertEquals("hyperbola", curve.string("type"))
            assertEquals(1.0, curve.double("cap"), 0.000001)
            assertEquals(10.0, curve.double("k"), 0.000001)
            assertEquals(0.0, curve.double("min"), 0.000001)
            assertEquals(1.0, curve.double("max"), 0.000001)
        }
    }

    @Test
    fun `still beating heart has a visible double pulse animation`() {
        val texturePath = Path.of(
            "src/main/resources/assets/rpg_stats/textures/item/still_beating_heart.png"
        )
        val metadataPath = texturePath.resolveSibling("still_beating_heart.png.mcmeta")
        val image = assertNotNull(ImageIO.read(texturePath.toFile()), "heart texture must be a readable PNG")

        assertEquals(16, image.width)
        assertEquals(64, image.height)

        val frameHeight = image.width
        val frameCount = image.height / frameHeight
        assertEquals(4, frameCount)
        val framePixels = (0 until frameCount).map { frame ->
            image.getRGB(0, frame * frameHeight, image.width, frameHeight, null, 0, image.width).toList()
        }
        assertEquals(frameCount, framePixels.distinct().size, "every heartbeat frame must be visually distinct")

        val metadata = Files.newBufferedReader(metadataPath).use {
            JsonParser.parseReader(it).asJsonObject
        }
        val animation = metadata.getAsJsonObject("animation")
        assertEquals(image.width, animation.int("width"))
        assertEquals(frameHeight, animation.int("height"))
        assertFalse(animation.get("interpolate").asBoolean, "direct frames keep the pulse visible at GUI scale")

        val scheduledFrames = animation.getAsJsonArray("frames").map { it.asJsonObject }
        assertTrue(scheduledFrames.size >= 9, "the schedule must include two beats and a rest")
        scheduledFrames.forEach { frame ->
            assertTrue(frame.int("index") in 0 until frameCount, "animation frame index must exist")
            assertTrue(frame.int("time") > 0, "animation frame duration must be positive")
        }
        assertTrue(scheduledFrames.count { it.int("index") == 2 } >= 2, "the peak frame must occur twice")
        assertEquals(0, scheduledFrames.first().int("index"))
        assertEquals(0, scheduledFrames.last().int("index"))
    }

    private fun validateCurve(curve: JsonObject, path: Path) {
        val type = curve.string("type")
        assertTrue(type in setOf("linear", "hyperbola", "exp", "exponential"), "unsupported curve in $path")
        if (type == "linear") {
            assertTrue(curve.double("per_point") > 0.0, "linear curve needs positive per_point in $path")
        }
    }

    private fun statJson(id: String): JsonObject =
        Files.newBufferedReader(Path.of("src/main/resources/data/rpg_stats/stats/$id.json")).use {
            JsonParser.parseReader(it).asJsonObject
        }

    private fun JsonObject.string(name: String): String = get(name).asString
    private fun JsonObject.int(name: String): Int = get(name).asInt
    private fun JsonObject.double(name: String): Double = get(name).asDouble
}
