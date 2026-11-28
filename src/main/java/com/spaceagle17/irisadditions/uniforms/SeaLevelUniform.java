package com.spaceagle17.irisadditions.uniforms;

//? if >=1.21.2 <1.21.11 {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/c9926beb6d3471f64104287f35186ce0fdbc3674
final class SeaLevelUniform {
    private SeaLevelUniform() {}

    // Older Minecraft versions do not tell the client the sea level, it always says 63 there
    static void register(UniformBackports<UniformHolder> backports) {
        backports.add("seaLevel", "1.9.4", (uniforms, name) ->
                uniforms.uniform1i(PER_FRAME, name, SeaLevelUniform::seaLevel));
    }

    private static int seaLevel() {
        var level = Minecraft.getInstance().level;
        return level == null ? 0 : level.getSeaLevel();
    }
}
*///?}
