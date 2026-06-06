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
    @Test
    fun `stat resources have matching names and valid attribute effects when present`() {
        val statsDir = Path.of("src/main/resources/data/rpgstats/stats")
        val statFiles = Files.list(statsDir).use { paths ->
            paths.filter { it.name.endsWith(".json") }.sorted().toList()
        }

        assertFalse(statFiles.isEmpty(), "expected stat JSON resources")

        statFiles.forEach { path ->
            val id = path.fileName.toString().removeSuffix(".json")
            val json = Files.newBufferedReader(path).use { JsonParser.parseReader(it).asJsonObject }

            assertEquals("stat.rpgstats.$id", json.string("name_key"))
            assertTrue(json.string("color").matches(Regex("#[0-9A-Fa-f]{6}")), "invalid color in $path")
            assertTrue(json.int("max_points") > 0, "max_points must be positive in $path")

            val effects = json.getAsJsonArray("effects")
            effects.forEach { element ->
                val effect = element.asJsonObject
                assertEquals("attribute", effect.string("type"), "unsupported effect type in $path")
                assertTrue(effect.string("attribute").contains(":"), "attribute must be namespaced in $path")
                assertTrue(effect.string("operation") in setOf("add", "multiply_base", "multiply_total"))
                validateCurve(effect.getAsJsonObject("curve"), path)
            }
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
