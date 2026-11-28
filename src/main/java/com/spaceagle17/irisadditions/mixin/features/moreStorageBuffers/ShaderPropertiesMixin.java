package com.spaceagle17.irisadditions.mixin.features.moreStorageBuffers;

//? if <=1.21.11 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/aaafb02916b7bf27276c10d76d739e3ee473a6b2
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = ShaderProperties.class, remap = false)
public abstract class ShaderPropertiesMixin {
    @Unique
    private boolean irisAdditions$limitLogged;

    // Check every (String, String) method and only the one of "bufferObject." has an 8
    @ModifyExpressionValue(
            method = "*(Ljava/lang/String;Ljava/lang/String;)V",
            at = @At(value = "CONSTANT", args = "intValue=8")
    )
    private int irisAdditions$raiseStorageBufferLimit(int limit) {
        int raised = Features.MORE_STORAGE_BUFFERS.isEnabled() ? Math.max(limit, 12) : limit;
        if (!irisAdditions$limitLogged) {
            irisAdditions$limitLogged = true;
            irisAdditions$debugLog(raised > limit
                    ? "Raised the highest storage buffer number from " + limit + " to " + raised
                    : "Leaving the highest storage buffer number at " + limit);
        }
        return raised;
    }

    @ModifyExpressionValue(
            method = "*(Ljava/lang/String;Ljava/lang/String;)V",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=SSBO's cannot use buffer numbers higher than 8, they're reserved!"
            )
    )
    private String irisAdditions$updateStorageBufferWarning(String warning) {
        return Features.MORE_STORAGE_BUFFERS.isEnabled() ? warning.replace(" 8,", " 12,") : warning;
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[ShaderPropertiesMixin] " + message);
    }
}
*///?}
