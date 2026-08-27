package com.bettercontent.rpgstats.common.salience

import com.bettercontent.rpgstats.RpgStatsMod
import net.minecraft.resources.ResourceLocation

enum class AspectIdentity(val index: Int, val glyph: String, val title: String, val color: Int) {
    IMPACT(0, "✦", "Impact", 0xE4717D),
    TEMPO(1, "»", "Tempo", 0xAA652B),
    WORK(2, "⚒", "Work", 0xCAA903),
    MOBILITY(3, "➜", "Mobility", 0xC0E304),
    ENDURANCE(4, "∞", "Endurance", 0x35BBD0),
    ROBUSTNESS(5, "◆", "Robustness", 0x1175FC),
    RENEWAL(6, "✚", "Renewal", 0x6FEDBA),
    CONTROL(7, "⊕", "Control", 0x8A6CB2);

    val label: String get() = "$glyph $title"
    val badge: String get() = (0xE100 + index).toChar().toString()

    companion object {
        val BADGES = ResourceLocation(RpgStatsMod.MODID, "textures/gui/aspect_badges.png")
        val FONT = ResourceLocation(RpgStatsMod.MODID, "aspects")
        fun fromStatId(id: String): AspectIdentity? = entries.firstOrNull {
            it.name.equals(id.substringAfter(':'), ignoreCase = true)
        }
    }
}
