package com.spaceagle17.irisadditions.uniforms;

//? if <1.21.6 {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_TICK;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/052faeb4c4382cc4d186ced14ec7a160615801e3
final class HeavyFogUniform {
    private HeavyFogUniform() {}

    // Every Iris release for 1.21.6 and newer has it
    static void register(UniformBackports<UniformHolder> backports) {
        backports.add("heavyFog", (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, HeavyFogUniform::isHeavyFog));
    }

    private static boolean isHeavyFog() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return false;

        BlockPos camera = client.gameRenderer.getMainCamera().getBlockPosition();
        return client.level.effects().isFoggyAt(camera.getX(), camera.getZ())
                || client.gui.getBossOverlay().shouldCreateWorldFog();
    }
}
*///?}
