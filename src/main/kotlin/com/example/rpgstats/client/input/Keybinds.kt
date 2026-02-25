package com.example.rpgstats.client.input

import com.example.rpgstats.RpgStatsMod
import com.example.rpgstats.client.ui.StatsScreen
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import org.lwjgl.glfw.GLFW

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object KeybindRegister {
    private val OPEN_STATS = KeyMapping(
        "key.rpgstats.open_stats",
        GLFW.GLFW_KEY_O,
        "key.categories.rpgstats"
    )

    @SubscribeEvent
    fun onRegisterKeybinds(event: RegisterKeyMappingsEvent) {
        event.register(OPEN_STATS)
    }

    fun mapping(): KeyMapping = OPEN_STATS
}

@Mod.EventBusSubscriber(modid = RpgStatsMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object Keybinds {
    fun init() {
        // no-op; events handle everything.
    }

    @SubscribeEvent
    fun onClientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        val mc = Minecraft.getInstance()
        if (mc.player == null) return

        if (KeybindRegister.mapping().consumeClick()) {
            if (mc.screen == null) {
                mc.setScreen(StatsScreen())
            } else if (mc.screen is StatsScreen) {
                mc.setScreen(null)
            }
        }
    }
}
