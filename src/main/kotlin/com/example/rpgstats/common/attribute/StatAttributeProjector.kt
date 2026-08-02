package com.example.rpgstats.common.attribute

import com.example.rpgstats.common.config.json.AttributeEffect
import com.example.rpgstats.common.curve.Curves
import com.example.rpgstats.common.data.StatsCap
import com.example.rpgstats.common.reload.RegistryState
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraftforge.registries.ForgeRegistries

object StatAttributeProjector {

    /**
     * Remove ALL our known modifiers (based on loaded defs), then add back the ones implied by allocations.
     * This avoids "stale modifier" bugs when a stat goes to 0 or is removed/reset.
     */
    fun reapply(player: ServerPlayer) {
        val stats = StatsCap.get(player) ?: return
        val defs = RegistryState.snapshot()

        // 1) Remove everything we own.
        for (def in defs.values) {
            val statIdStr = def.id.toString()
            for (effect in def.effects) {
                if (effect is AttributeEffect) {
                    val attr = ForgeRegistries.ATTRIBUTES.getValue(effect.attributeId) ?: continue
                    val inst: AttributeInstance = player.getAttribute(attr) ?: continue
                    val uuid = ModifierUuids.uuidFor(statIdStr, effect.attributeId.toString())
                    inst.removeModifier(uuid)
                }
            }
        }

        // 2) Add current projections.
        for ((statIdStr, pts) in stats.allocations) {
            if (pts <= 0) continue
            val statId = ResourceLocation.tryParse(statIdStr) ?: continue
            val def = defs[statId] ?: continue

            for (effect in def.effects) {
                if (effect is AttributeEffect) {
                    val attr = ForgeRegistries.ATTRIBUTES.getValue(effect.attributeId) ?: continue
                    val inst: AttributeInstance = player.getAttribute(attr) ?: continue

                    val uuid = ModifierUuids.uuidFor(statIdStr, effect.attributeId.toString())
                    val amount = Curves.eval(pts, effect.curve)
                    if (amount == 0.0) continue

                    val mod = AttributeModifier(uuid, "rpgstats:$statIdStr", amount, effect.operation)
                    inst.addTransientModifier(mod)
                }
            }
        }
    }
}
