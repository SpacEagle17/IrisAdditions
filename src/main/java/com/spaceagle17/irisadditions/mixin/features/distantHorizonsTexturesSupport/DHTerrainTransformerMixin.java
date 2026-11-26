package com.spaceagle17.irisadditions.mixin.features.distantHorizonsTexturesSupport;

import com.spaceagle17.irisadditions.core.feature.Features;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.ast.transform.ASTParser;
import net.irisshaders.iris.gl.shader.ShaderType;
import net.irisshaders.iris.pipeline.transform.parameter.Parameters;
import net.irisshaders.iris.pipeline.transform.transformer.DHTerrainTransformer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Passes on the texture of each Distant Horizons vertex and adds the functions to sample it
@Mixin(value = DHTerrainTransformer.class, remap = false)
public abstract class DHTerrainTransformerMixin {
    @Inject(method = "transform", at = @At("TAIL"))
    private static void irisAdditions$addBlockTextures(ASTParser t, TranslationUnit tree, Root root,
                                                       Parameters parameters, CallbackInfo ci) {
        if (!Features.DISTANT_HORIZONS_TEXTURES.isEnabled()) return;

        if (parameters.type.glShaderType == ShaderType.VERTEX) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS,
                    "out vec3 iris_vBlockPos;",
                    "flat out uvec2 iris_TexId;");
            // Tile id and face direction, which Iris leaves unused in the third and fourth byte
            tree.prependMainFunctionBody(t,
                    "iris_vBlockPos = vec3(vPosition.xyz);",
                    "iris_TexId = uvec2(irisExtra.z | (irisExtra.w << 8u), irisExtra.y);");
        } else if (parameters.type.glShaderType == ShaderType.FRAGMENT) {
            tree.parseAndInjectNodes(t, ASTInjectionPoint.BEFORE_DECLARATIONS,
                    "in vec3 iris_vBlockPos;",
                    "flat in uvec2 iris_TexId;",
                    "uniform sampler2D dhBlockAtlas;",
                    "bool dh_hasTexture() { return iris_TexId.x != 0u; }",
                    """
                    vec2 irisAdditions_faceUv(vec3 pos) {
                        switch (iris_TexId.y) {
                            case 0u: return vec2(pos.x, 1.0 - pos.z); // down
                            case 1u: return vec2(pos.x, pos.z); // up
                            case 2u: return vec2(1.0 - pos.x, 1.0 - pos.y); // north
                            case 3u: return vec2(pos.x, 1.0 - pos.y); // south
                            case 4u: return vec2(pos.z, 1.0 - pos.y); // west
                            default: return vec2(1.0 - pos.z, 1.0 - pos.y); // east
                        }
                    }""",
                    "vec2 dh_blockFaceUv() { return irisAdditions_faceUv(fract(iris_vBlockPos)); }",
                    // The mip level comes from the position without fract(), which jumps at every block edge
                    // and would draw lines there. Distant Horizons itself does the same
                    """
                    vec4 dh_sampleTexture() {
                        vec2 atlasSize = vec2(textureSize(dhBlockAtlas, 0));
                        vec2 tileOrigin = vec2(float(iris_TexId.x % 256u), float(iris_TexId.x / 256u)) * 16.0;
                        vec2 uv = (tileOrigin + dh_blockFaceUv() * 16.0) / atlasSize;
                        vec2 smoothUv = irisAdditions_faceUv(iris_vBlockPos) * 16.0 / atlasSize;
                        return textureGrad(dhBlockAtlas, uv, dFdx(smoothUv), dFdy(smoothUv));
                    }""");
        }
    }
}
