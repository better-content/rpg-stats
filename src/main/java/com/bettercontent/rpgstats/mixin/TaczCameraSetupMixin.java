package com.bettercontent.rpgstats.mixin;

import com.bettercontent.rpgstats.common.attribute.ControlScaling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "com.tacz.guns.client.event.CameraSetupEvent", remap = false)
abstract class TaczCameraSetupMixin {
    @ModifyArg(
        method = "initialCameraRecoil",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genPitchSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;",
            remap = false
        ),
        index = 0,
        require = 0
    )
    private static float rpg_stats$reducePitchRecoil(float recoil) {
        return rpg_stats$reduceRecoil(recoil);
    }

    @ModifyArg(
        method = "initialCameraRecoil",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tacz/guns/resource/pojo/data/gun/GunRecoil;genYawSplineFunction(F)Lorg/apache/commons/math3/analysis/polynomials/PolynomialSplineFunction;",
            remap = false
        ),
        index = 0,
        require = 0
    )
    private static float rpg_stats$reduceYawRecoil(float recoil) {
        return rpg_stats$reduceRecoil(recoil);
    }

    private static float rpg_stats$reduceRecoil(float recoil) {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? recoil : ControlScaling.scaleRecoil(recoil, player);
    }
}
