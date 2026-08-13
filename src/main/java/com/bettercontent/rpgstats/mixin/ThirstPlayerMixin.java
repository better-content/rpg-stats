package com.bettercontent.rpgstats.mixin;

import com.bettercontent.rpgstats.common.attribute.EfficiencyScaling;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.ghen.thirst.content.thirst.PlayerThirst", remap = false)
abstract class ThirstPlayerMixin {
    @Unique
    private Player rpg_stats$player;

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void rpg_stats$rememberPlayer(Player player, CallbackInfo callback) {
        rpg_stats$player = player;
    }

    @ModifyVariable(
        method = "addExhaustion",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0,
        require = 0
    )
    private float rpg_stats$scaleThirstExhaustion(float amount) {
        return rpg_stats$player == null ? amount : EfficiencyScaling.scaleThirst(amount, rpg_stats$player);
    }

    @ModifyArg(
        method = "updateExhaustion",
        at = @At(
            value = "INVOKE",
            target = "Ldev/ghen/thirst/content/thirst/PlayerThirst;addExhaustion(Lnet/minecraft/world/entity/player/Player;F)V"
        ),
        index = 1,
        require = 0
    )
    private float rpg_stats$restoreHungerScale(float amount) {
        return rpg_stats$player == null ? amount : EfficiencyScaling.restoreMirroredHungerScale(amount, rpg_stats$player);
    }
}
