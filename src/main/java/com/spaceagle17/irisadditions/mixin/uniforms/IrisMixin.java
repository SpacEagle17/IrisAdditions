package com.spaceagle17.irisadditions.mixin.uniforms;

//? if <=1.21.11 {
/*import com.spaceagle17.irisadditions.uniforms.TextureUniforms;
import net.irisshaders.iris.Iris;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = Iris.class, remap = false)
public abstract class IrisMixin {
    @Inject(method = "reload", at = @At("HEAD"))
    private static void irisAdditions$resetTextureReloadCount(CallbackInfo ci) {
        TextureUniforms.onShaderPackReload();
    }
}
*///?}
