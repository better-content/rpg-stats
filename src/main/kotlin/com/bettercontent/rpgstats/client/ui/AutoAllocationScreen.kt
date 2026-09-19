package com.bettercontent.rpgstats.client.ui

import com.bettercontent.rpgstats.client.cache.ClientCache
import com.bettercontent.rpgstats.client.cache.ClientStatDef
import com.bettercontent.rpgstats.common.network.Network
import com.bettercontent.rpgstats.common.network.packets.C2SAutoAllocationPlan
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

/** Small explicit editor for the character-owned ordered allocation plan. */
class AutoAllocationScreen(private val returnTo: Screen) : Screen(Component.translatable("screen.rpg_stats.auto_plan")) {
    private val plan = ClientCache.stats.autoAllocationPlan.toMutableList()
    private var enabled = ClientCache.stats.autoAllocationEnabled

    override fun init() {
        super.init()
        val left = width / 2 - 150
        val top = height / 2 - 92
        addRenderableWidget(Button.builder(enabledLabel()) { button ->
            enabled = !enabled
            button.message = enabledLabel()
        }.pos(left, top).size(110, 20).build())
        addRenderableWidget(Button.builder(Component.translatable("screen.rpg_stats.clear_plan")) {
            plan.clear()
            refreshEditor()
        }.pos(left + 116, top).size(88, 20).build())
        addRenderableWidget(Button.builder(Component.translatable("screen.rpg_stats.done")) {
            Network.sendToServer(C2SAutoAllocationPlan(enabled, plan.toList()))
            minecraft?.setScreen(returnTo)
        }.pos(left + 210, top).size(90, 20).build())
        rebuildRows()
    }

    private fun refreshEditor() {
        clearWidgets()
        init()
    }

    private fun rebuildRows() {
        val left = width / 2 - 150
        val top = height / 2 - 64
        ClientCache.defs.forEachIndexed { index, def ->
            val y = top + index * 19
            val present = def.id in plan
            addRenderableWidget(Button.builder(Component.translatable(if (present) "screen.rpg_stats.remove_plan" else "screen.rpg_stats.add_plan")) {
                if (present) plan.remove(def.id) else plan.add(def.id)
                refreshEditor()
            }.pos(left, y).size(58, 18).build())
            addRenderableWidget(Button.builder(Component.translatable(def.nameKey)) { }
                .pos(left + 62, y).size(168, 18).build().also { it.active = false })
            if (present) {
                val ordinal = plan.indexOf(def.id)
                addRenderableWidget(Button.builder(Component.literal("↑")) {
                    if (ordinal > 0) {
                        plan[ordinal] = plan[ordinal - 1].also { plan[ordinal - 1] = plan[ordinal] }
                        refreshEditor()
                    }
                }.pos(left + 234, y).size(30, 18).build().also { it.active = ordinal > 0 })
                addRenderableWidget(Button.builder(Component.literal("↓")) {
                    if (ordinal < plan.lastIndex) {
                        plan[ordinal] = plan[ordinal + 1].also { plan[ordinal + 1] = plan[ordinal] }
                        refreshEditor()
                    }
                }.pos(left + 268, y).size(30, 18).build().also { it.active = ordinal < plan.lastIndex })
            }
        }
    }

    private fun enabledLabel(): Component = Component.translatable(
        if (enabled) "screen.rpg_stats.auto_enabled" else "screen.rpg_stats.auto_paused"
    )

    override fun onClose() { minecraft?.setScreen(returnTo) }
}
