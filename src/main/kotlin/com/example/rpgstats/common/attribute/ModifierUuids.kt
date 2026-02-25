package com.example.rpgstats.common.attribute

import java.nio.charset.StandardCharsets
import java.util.UUID

object ModifierUuids {
    fun uuidFor(statId: String, attributeId: String): UUID {
        val key = "rpgstats:$statId->$attributeId"
        return UUID.nameUUIDFromBytes(key.toByteArray(StandardCharsets.UTF_8))
    }
}
