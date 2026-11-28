package com.spaceagle17.irisadditions.mixin.fixes.horizonPerformanceFix;

//? if >=1.21.5 <=1.21.11 {
/*import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.spaceagle17.irisadditions.core.feature.Fixes;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.pathways.HorizonRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adapted from Iris by LovelyCane (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/pull/2941
// Iris builds the horizon out of quads, but since Minecraft 1.21.5 the sky pipeline draws a triangle fan.
// That draws many more and much bigger triangles than meant, so build a shape made for a triangle fan
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = HorizonRenderer.class, remap = false)
public abstract class HorizonRendererMixin {
    // Only changed when the shape is built, so that it is always drawn the way it was built
    @Unique
    private boolean irisAdditions$useFan;

    @Inject(method = "rebuildBuffer", at = @At("HEAD"))
    private void irisAdditions$chooseShape(CallbackInfo ci) {
        irisAdditions$useFan = Fixes.HORIZON_PERFORMANCE.isEnabled();
        irisAdditions$debugLog("Building the horizon, as a triangle fan: " + irisAdditions$useFan);
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[HorizonRendererMixin] " + message);
    }

    @ModifyArg(
            method = "rebuildBuffer",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/Tesselator;begin(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;Lcom/mojang/blaze3d/vertex/VertexFormat;)Lcom/mojang/blaze3d/vertex/BufferBuilder;",
                    remap = true
            ),
            index = 0
    )
    private VertexFormat.Mode irisAdditions$buildAsFan(VertexFormat.Mode mode) {
        return irisAdditions$useFan ? VertexFormat.Mode.TRIANGLE_FAN : mode;
    }

    // An octagonal cone, from one point below the player up to a ring above
    @Inject(method = "buildHorizon", at = @At("HEAD"), cancellable = true)
    private void irisAdditions$buildCone(int radius, VertexConsumer consumer, CallbackInfo ci) {
        if (!irisAdditions$useFan) return;

        // Prevent the cone from getting too large, this causes issues on some shader packs that modify the vanilla
        int cappedRadius = Math.min(radius, 256);
        consumer.addVertex(0.0F, -16.0F, 0.0F);
        for (int i = 0; i <= 8; i++) {
            float angle = (float) (-i * Math.PI / 4.0);
            float x = (float) (cappedRadius * Math.cos(angle));
            float z = (float) (cappedRadius * Math.sin(angle));
            consumer.addVertex(x, 16.0F, z);
        }
        ci.cancel();
    }

    @ModifyArg(
            method = "renderHorizon",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;getSequentialBuffer(Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;)Lcom/mojang/blaze3d/systems/RenderSystem$AutoStorageIndexBuffer;",
                    remap = true
            )
    )
    private VertexFormat.Mode irisAdditions$drawAsFan(VertexFormat.Mode mode) {
        return irisAdditions$useFan ? VertexFormat.Mode.TRIANGLE_FAN : mode;
    }
}
*///?}
