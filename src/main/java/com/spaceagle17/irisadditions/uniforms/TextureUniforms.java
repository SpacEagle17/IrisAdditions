package com.spaceagle17.irisadditions.uniforms;

//? if <=1.21.11 {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import com.spaceagle17.irisadditions.mixin.uniforms.TextureStateAccessor;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.mixin.GlStateManagerAccessor;

// Adapted from Iris by xirreal (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/pull/3028
public final class TextureUniforms {
    private static int reloadCount;

    private TextureUniforms() {}

    public static void onTexturesReloaded() {
        reloadCount++;
    }

    public static void onShaderPackReload() {
        reloadCount = 0;
    }

    static void register(UniformBackports<DynamicUniformHolder> backports) {
        String since = "1.10.7";

        backports.add("textureReloadCount", since, (uniforms, name) ->
                uniforms.uniform1i(name, () -> reloadCount, listener -> {}));
        backports.add("gtextureId", since, (uniforms, name) ->
                uniforms.uniform1i(name, TextureUniforms::boundTextureId, listener -> {}));
    }

    private static int boundTextureId() {
        Object[] textures = GlStateManagerAccessor.getTEXTURES();
        return ((TextureStateAccessor) textures[0]).irisAdditions$getBinding();
    }
}
*///?}
