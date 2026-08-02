package com.example.rpgstats.common.resource

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.name
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
        val statsDir = Path.of("src/main/resources/data/rpgstats/stats")
        val statFiles = Files.list(statsDir).use { paths ->
            paths.filter { it.name.endsWith(".json") }.sorted().toList()
        }

        assertEquals(expectedStats, statFiles.map { it.name.removeSuffix(".json") }.toSet())

        statFiles.forEach { path ->
            val id = path.fileName.toString().removeSuffix(".json")
            val json = Files.newBufferedReader(path).use { JsonParser.parseReader(it).asJsonObject }

            assertEquals("stat.rpgstats.$id", json.string("name_key"))
            assertTrue(json.string("color").matches(Regex("#[0-9A-Fa-f]{6}")), "invalid color in $path")
            assertTrue(json.int("max_points") > 0, "max_points must be positive in $path")

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
            val path = Path.of("src/main/resources/data/rpgstats/stats/$id.json")
            val json = Files.newBufferedReader(path).use { JsonParser.parseReader(it).asJsonObject }
            val effect = json.getAsJsonArray("effects").single().asJsonObject
            assertEquals("rpgstats:$id", effect.string("attribute"))
            assertEquals("multiply_base", effect.string("operation"))
            assertEquals(0.04, effect.getAsJsonObject("curve").double("per_point"), 0.000001)
        }
    }

    private fun validateCurve(curve: JsonObject, path: Path) {
        val type = curve.string("type")
        assertTrue(type in setOf("linear", "hyperbola", "exp", "exponential"), "unsupported curve in $path")
        if (type == "linear") {
            assertTrue(curve.double("per_point") > 0.0, "linear curve needs positive per_point in $path")
        }
    }

    private fun JsonObject.string(name: String): String = get(name).asString
    private fun JsonObject.int(name: String): Int = get(name).asInt
    private fun JsonObject.double(name: String): Double = get(name).asDouble
}
