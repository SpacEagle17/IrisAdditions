package com.spaceagle17.irisadditions.mixin.uniforms;

//? if <=1.21.11 {
/*import com.spaceagle17.irisadditions.uniforms.TextureUniforms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Iris moved this class in 1.8.0, so 1.21.1 needs both places
@SuppressWarnings({"unused", "UnusedMixin"})
@Pseudo
@Mixin(targets = {
        //? if <1.21.2 {
        /^"net.irisshaders.iris.texture.pbr.PBRTextureManager",
        ^///?}
        //? if >=1.21 {
        "net.irisshaders.iris.pbr.texture.PBRTextureManager",
        //?}
}, remap = false)
public abstract class PBRTextureManagerMixin {
    // Iris calls this whenever Minecraft reloaded its textures
    @Inject(method = "clear", at = @At("TAIL"))
    private void irisAdditions$countTextureReload(CallbackInfo ci) {
        TextureUniforms.onTexturesReloaded();
    }
}
*///?}
