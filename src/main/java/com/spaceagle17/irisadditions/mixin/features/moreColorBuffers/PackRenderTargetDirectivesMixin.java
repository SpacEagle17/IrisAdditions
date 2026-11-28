package com.spaceagle17.irisadditions.mixin.features.moreColorBuffers;

//? if <=1.21.11 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.shaderpack.properties.PackRenderTargetDirectives;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/b7c084d24c542a1f2c9982ed75fd6225ef471af3
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = PackRenderTargetDirectives.class, remap = false)
public abstract class PackRenderTargetDirectivesMixin {
    @Unique
    private static boolean irisAdditions$countLogged;

    // Only add
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "CONSTANT", args = "intValue=16"))
    private static int irisAdditions$raiseColorBufferCount(int count) {
        int raised = Features.MORE_COLOR_BUFFERS.isEnabled() ? Math.max(count, 32) : count;
        if (!irisAdditions$countLogged) {
            irisAdditions$countLogged = true;
            irisAdditions$debugLog(raised > count
                    ? "Extended colortex amount from " + count + " to " + raised
                    : "Leaving the colortex amount at " + count);
        }
        return raised;
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[PackRenderTargetDirectivesMixin] " + message);
    }
}
*///?}
