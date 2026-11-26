package com.spaceagle17.irisadditions.mixin.features.distantHorizonsTexturesSupport;

import com.seibel.distanthorizons.api.DhApi;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import net.irisshaders.iris.compat.dh.IrisLodRenderProgram;
import net.irisshaders.iris.gl.IrisRenderSystem;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adapted from Iris by IMS212 (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/5b89e916bf228b202e8b8b26df6742149be3b954
// Gives the shader the block texture atlas of Distant Horizons
@Mixin(value = IrisLodRenderProgram.class, remap = false)
public abstract class IrisLodRenderProgramMixin {
    @Shadow
    @Final
    private int id;

    @Unique
    private int irisAdditions$blockAtlasLocation;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void irisAdditions$findBlockAtlas(CallbackInfo ci) {
        irisAdditions$blockAtlasLocation = GL20C.glGetUniformLocation(id, "dhBlockAtlas");
    }

    @Inject(method = "bind", at = @At("TAIL"))
    private void irisAdditions$bindBlockAtlas(CallbackInfo ci) {
        if (irisAdditions$blockAtlasLocation == -1) return;

        // Texture unit 0, which Distant Horizons shaders in Iris do not use
        GL20C.glUniform1i(irisAdditions$blockAtlasLocation, 0);
        DhApiResult<Integer> atlas = DhApi.Delayed.renderProxy.getDhBlockRatioAtlasTextureGlId();
        // Fails as long as textured LODs are turned off in Distant Horizons
        if (atlas.success) {
            IrisRenderSystem.bindTextureToUnit(GL11C.GL_TEXTURE_2D, 0, atlas.payload);
        }
    }
}
