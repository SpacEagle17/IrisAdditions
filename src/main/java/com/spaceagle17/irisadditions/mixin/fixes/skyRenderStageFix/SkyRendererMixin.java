package com.spaceagle17.irisadditions.mixin.fixes.skyRenderStageFix;

//? if >=1.21.4 <=1.21.8 {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.spaceagle17.irisadditions.fixes.SkyStageShard;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Adapted from Iris by IMS212 (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/c00471baff637f81e3eb0f5f028da19ab2cc3ef3
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(SkyRenderer.class)
public abstract class SkyRendererMixin {
    @WrapOperation(
            method = "renderSun",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer irisAdditions$sunStage(MultiBufferSource source, RenderType type,
                                                  Operation<VertexConsumer> original) {
        return original.call(source, SkyStageShard.SUN.wrap(type));
    }

    @WrapOperation(
            method = "renderSunriseAndSunset",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer irisAdditions$sunsetStage(MultiBufferSource.BufferSource source, RenderType type,
                                                     Operation<VertexConsumer> original) {
        return original.call(source, SkyStageShard.SUNSET.wrap(type));
    }

    @WrapOperation(
            method = "renderMoon",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            )
    )
    private VertexConsumer irisAdditions$moonStage(MultiBufferSource source, RenderType type,
                                                   Operation<VertexConsumer> original) {
        return original.call(source, SkyStageShard.MOON.wrap(type));
    }
}
*///?}
