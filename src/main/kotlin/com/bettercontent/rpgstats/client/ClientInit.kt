package com.bettercontent.rpgstats.client

import com.bettercontent.rpgstats.client.input.Keybinds

object ClientInit {
    fun init() {
        Keybinds.init()
        installVisualHarnessIfRequested()
    }

    private fun installVisualHarnessIfRequested() {
        if (!java.lang.Boolean.getBoolean("rpg_stats.visualHarness")) return

        Class.forName("com.bettercontent.rpgstats.harness.RpgStatsVisualHarness")
            .getMethod("install")
            .invoke(null)
    }
}
