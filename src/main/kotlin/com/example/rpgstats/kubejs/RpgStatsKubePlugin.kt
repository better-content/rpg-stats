package com.example.rpgstats.kubejs

import com.example.rpgstats.common.heart.HeartTypeRegistry
import dev.latvian.mods.kubejs.KubeJSPlugin
import dev.latvian.mods.kubejs.client.LangEventJS
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator
import dev.latvian.mods.kubejs.script.ScriptType
import org.apache.logging.log4j.LogManager
import java.util.Locale

class RpgStatsKubePlugin : KubeJSPlugin() {
    private val logger = LogManager.getLogger("RPGStats/KubeJS")

    override fun registerEvents() {
        RpgStatsKubeEvents.GROUP.register()
    }

    override fun initStartup() {
        val event = HeartTypesStartupEventJS()
        RpgStatsKubeEvents.HEART_TYPES.post(ScriptType.STARTUP, event)
        val defs = event.buildDefinitions()
        HeartTypeRegistry.replaceFromKubeJs(defs)
    }

    override fun generateAssetJsons(generator: AssetJsonGenerator) {
        for (def in HeartTypeRegistry.definitions()) {
            generator.itemModel(def.outputItemId) { model ->
                model.parent("minecraft:item/generated")
                model.texture("layer0", def.assetTexture.toString())
            }
        }
    }

    override fun generateLang(event: LangEventJS) {
        if (event.lang.lowercase(Locale.ROOT) != "en_us") return
        for (def in HeartTypeRegistry.definitions()) {
            val display = def.displayName ?: defaultDisplayName(def.typeId.path)
            event.add("item.${def.outputItemId.namespace}.${def.outputItemId.path}", display)
        }
        logger.info("Generated {} typed heart lang entries.", HeartTypeRegistry.definitions().size)
    }

    private fun defaultDisplayName(path: String): String {
        return path
            .split('_')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString() }
            }
            .ifBlank { "Typed Heart" }
    }
}
