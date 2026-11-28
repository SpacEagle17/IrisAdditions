package com.spaceagle17.irisadditions.mixin.features.clampedEndFlashAngle;

//? if >=1.21.9 <=26.2 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.spaceagle17.irisadditions.core.feature.Features;
import net.irisshaders.iris.Iris;
import net.minecraft.client.renderer.EndFlashState;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Adapted from Iris by Acer (Alex) / djmrFunnyMan and also IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/pull/3082
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(EndFlashState.class)
public abstract class EndFlashStateMixin {
    // First is the angle above the horizon
    @WrapOperation(
            method = "calculateFlashParameters",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;randomBetween(Lnet/minecraft/util/RandomSource;FF)F",
                    ordinal = 0
            )
    )
    private float irisAdditions$clampAngle(RandomSource random, float min, float max, Operation<Float> original) {
        if (!Features.CLAMPED_END_FLASH_ANGLE.isEnabled() || !Iris.getCurrentPack().isPresent()) {
            return original.call(random, min, max);
        }
        return -original.call(random, 5.0F, 60.0F);
    }
}
*///?}
