package com.example.rpgstats.client.ui

import com.example.rpgstats.RpgStatsMod
import com.example.rpgstats.client.cache.ClientCache
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RenderGuiOverlayEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object UnspentPointsOverlay {
    
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGuiOverlayEvent.Post) {
        val mc = Minecraft.getInstance()
        if (mc.player == null || mc.isPaused) return
        
        val unspent = ClientCache.stats.unspent
        if (unspent <= 0) return
        
        val guiGraphics = event.guiGraphics
        val font = mc.font
        
        // Annoying reminder text
        val text1 = Component.translatable("overlay.rpgstats.unspent_points", unspent.toString())
        val text2 = Component.translatable("overlay.rpgstats.press_key", "O")
        
        // Position in top-right corner with some padding
        val padding = 10
        val x = event.window.guiScaledWidth - font.width(text1) - padding
        val y = padding
        
        // Draw with a semi-transparent black background for readability
        val bgColor = 0x80000000.toInt()
        val textColor = 0xFFFF55 // Annoying yellow color
        
        // Draw background boxes
        guiGraphics.fill(x - 2, y - 2, x + font.width(text1) + 2, y + font.lineHeight + 2, bgColor)
        guiGraphics.fill(x - 2, y + font.lineHeight, x + font.width(text2) + 2, y + font.lineHeight * 2 + 2, bgColor)
        
        // Draw text
        guiGraphics.drawString(font, text1, x, y, textColor)
        guiGraphics.drawString(font, text2, x, y + font.lineHeight, textColor)
    }
}
