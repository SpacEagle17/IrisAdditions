package com.spaceagle17.irisadditions.uniforms;

//? if <26.3 {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/0a708682a00cf009dba383945670dcda979de7ae
final class GlintAlphaUniform {
    private GlintAlphaUniform() {}

    static void register(UniformBackports<UniformHolder> backports) {
        backports.add("glintAlpha", (uniforms, name) ->
                uniforms.uniform1f(PER_FRAME, name, () -> Minecraft.getInstance().options.glintStrength().get()));
    }
}
*///?}
