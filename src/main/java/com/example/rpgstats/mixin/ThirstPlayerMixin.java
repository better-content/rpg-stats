package com.example.rpgstats.mixin;

import com.example.rpgstats.common.attribute.EfficiencyScaling;
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
    private Player rpgstats$player;

    @Inject(method = "tick", at = @At("HEAD"), require = 0)
    private void rpgstats$rememberPlayer(Player player, CallbackInfo callback) {
        rpgstats$player = player;
    }

    @ModifyVariable(
        method = "addExhaustion",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0,
        require = 0
    )
    private float rpgstats$scaleThirstExhaustion(float amount) {
        return rpgstats$player == null ? amount : EfficiencyScaling.scaleThirst(amount, rpgstats$player);
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
    private float rpgstats$restoreHungerScale(float amount) {
        return rpgstats$player == null ? amount : EfficiencyScaling.restoreMirroredHungerScale(amount, rpgstats$player);
    }
}
