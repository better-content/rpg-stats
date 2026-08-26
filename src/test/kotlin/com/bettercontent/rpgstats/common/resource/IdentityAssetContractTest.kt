package com.bettercontent.rpgstats.common.resource

import com.bettercontent.rpgstats.common.salience.AspectIdentity
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.security.MessageDigest
import java.util.HexFormat
import javax.imageio.ImageIO

class IdentityAssetContractTest {
    private val motifHashes = mapOf(
        "impact" to "73f2a5256be3a4f0c85ab65aeaa6d139ac43103e8e8d24003b9a4119ea2ab51a",
        "tempo" to "e77950b8b2e29f7425af7ae1673ed12f325de741af149b1b308bb6af64d44665",
        "work" to "aeabd6b7a437211ff683fbb452e5671b84d106d8daab63c7333869e3f4cf8a3e",
        "mobility" to "ba1f67679ec62909c6bfb16532efed80e736c258ad9582037df423c04157a570",
        "endurance" to "6409e26c4f80c0586dc220542cd4a18d44089a3d7bda8ea79af94b5aa5d7e64d",
        "robustness" to "e914400a5a03a898528a1d6674398e20fca5d5778ea4c8f617df23958d780311",
        "renewal" to "6fe2b992a4ffd3bead0992d53bc6885ad86e24e0fd71e84f67b6f812ca061a21",
        "control" to "971dee8ac79c5961407651e16ac73821f8864923738d8b6c8d637e001e07d457"
    )

    @Test
    fun `badge font and motifs cover the exact identity contract`() {
        val badge = resource("/assets/rpg_stats/textures/gui/aspect_badges.png")
        assertEquals("b59717a5da26f633cd120f09b750c15875577ec9c74a8c7838c32aea8ee5eeed",
            HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(badge)))
        ImageIO.read(badge.inputStream()).also { image -> assertEquals(144, image.width); assertEquals(18, image.height) }
        val font = resource("/assets/rpg_stats/font/aspects.json").decodeToString()
        assertTrue(font.contains(""))
        val sounds = resource("/assets/rpg_stats/sounds.json").decodeToString()
        AspectIdentity.entries.forEachIndexed { index, aspect ->
            assertEquals(index, aspect.index)
            val id = aspect.name.lowercase()
            assertTrue(sounds.contains("aspect.$id"))
            val ogg = resource("/assets/rpg_stats/sounds/aspect/$id.ogg")
            assertTrue(ogg.size > 4000)
            assertArrayEquals("OggS".encodeToByteArray(), ogg.copyOfRange(0, 4))
            assertEquals(motifHashes[id], HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(ogg)), id)
        }
    }

    private fun resource(path: String): ByteArray = javaClass.getResourceAsStream(path).also { assertNotNull(it, path) }!!.readAllBytes()
}
