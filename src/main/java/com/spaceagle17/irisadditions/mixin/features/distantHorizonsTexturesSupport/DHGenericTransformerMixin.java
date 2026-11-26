package com.spaceagle17.irisadditions.mixin.features.distantHorizonsTexturesSupport;

import com.spaceagle17.irisadditions.core.feature.Features;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.pipeline.transform.transformer.DHGenericTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Clouds and other generic objects have no textures, but shaders may share code with the terrain
@Mixin(value = DHGenericTransformer.class, remap = false)
public abstract class DHGenericTransformerMixin {
    @Inject(method = "transform", at = @At("TAIL"))
    private static void irisAdditions$addTextureFallbacks(ASTParser t, TranslationUnit tree, Root root,
                                                          Parameters parameters, CallbackInfo ci) {
        if (!Features.DISTANT_HORIZONS_TEXTURES.isEnabled()) return;

        if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS,
                    "bool dh_hasTexture() { return false; }",
                    "vec4 dh_sampleTexture() { return vec4(1.0); }");
        }
    }
}
