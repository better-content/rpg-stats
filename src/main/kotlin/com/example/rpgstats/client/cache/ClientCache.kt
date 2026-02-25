package com.example.rpgstats.client.cache

data class ClientStatsSnapshot(
    val unspent: Int = 0,
    val lifePeak: Int = 0,
    val allocations: Map<String, Int> = emptyMap()
)

data class ClientCurveDef(
    val type: String,
    val cap: Double,
    val k: Double,
    val perPoint: Double,
    val min: Double,
    val max: Double
)

data class ClientEffectDef(
    val attributeId: String,
    val operation: Int,
    val curve: ClientCurveDef,
    val isPrimary: Boolean = true
)

data class ClientStatDef(
    val id: String,
    val nameKey: String,
    val maxPoints: Int,
    val effects: List<ClientEffectDef>,
    val icon: String = "",
    val color: Int = 0xFFFFFFFF.toInt()
)

object ClientCache {
    @Volatile var defs: List<ClientStatDef> = emptyList()
    @Volatile var stats: ClientStatsSnapshot = ClientStatsSnapshot()
}
