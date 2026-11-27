package com.spaceagle17.irisadditions.mixin.features.shorterHoverTime;

//? if <=1.21.11 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.spaceagle17.irisadditions.core.feature.Features;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// https://github.com/IrisShaders/Iris/commit/67e291c518cd3bd7b6393bb9a00c60c8f64d6e42
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin {
    @ModifyExpressionValue(method = "isDisplayingComment", at = @At(value = "CONSTANT", args = "intValue=20"))
    private int irisAdditions$shortenHoverTime(int ticks) {
        return Features.SHORTER_HOVER_TIME.isEnabled() ? 10 : ticks;
    }
}
*///?}
