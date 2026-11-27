package com.spaceagle17.irisadditions.mixin.uniforms;

import com.spaceagle17.irisadditions.uniforms.ShaderUniforms;
import net.irisshaders.iris.gl.state.FogMode;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.CommonUniforms;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CommonUniforms.class, remap = false)
public abstract class CommonUniformsMixin {
    @Inject(method = "addNonDynamicUniforms", at = @At("TAIL"))
    private static void irisAdditions$addUniforms(UniformHolder uniforms, IdMap idMap, PackDirectives directives,
                                                  FrameUpdateNotifier updateNotifier, CallbackInfo ci) {
        ShaderUniforms.addUniforms(uniforms);
    }

    @Inject(method = "addDynamicUniforms", at = @At("TAIL"))
    private static void irisAdditions$addDynamicUniforms(DynamicUniformHolder uniforms, FogMode fogMode,
                                                         CallbackInfo ci) {
        ShaderUniforms.addDynamicUniforms(uniforms);
    }
}
