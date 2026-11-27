package com.spaceagle17.irisadditions.uniforms;

//? if forge {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import com.spaceagle17.irisadditions.mixin.uniforms.LocalPlayerAccessor;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientMoodSettings;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_TICK;

// Adapted from Iris by IMS212 (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/e9c00310ab644cfc88928502807cbb77632140b3
// Like playerMood, but it does not reset after 1.0
public final class ConstantMoodUniform {
    public interface Source {
        float irisAdditions$getConstantMood();
    }

    private ConstantMoodUniform() {}

    // Only Oculus lacks it
    static void register(UniformBackports<UniformHolder> backports) {
        backports.add("constantMood", (uniforms, name) ->
                uniforms.uniform1f(PER_TICK, name, ConstantMoodUniform::currentMood));
    }

    public static float nextMood(float mood, Level level, AmbientMoodSettings settings, BlockPos pos) {
        int skyLight = level.getBrightness(LightLayer.SKY, pos);
        if (skyLight > 0) {
            mood -= (float) skyLight / level.getMaxLightLevel() * 0.001f;
        } else {
            mood -= (float) (level.getBrightness(LightLayer.BLOCK, pos) - 1) / settings.getTickDelay();
        }
        return Mth.clamp(mood, 0.0f, 1.0f);
    }

    private static float currentMood() {
        if (!(Minecraft.getInstance().cameraEntity instanceof LocalPlayer player)) return 0.0f;

        for (AmbientSoundHandler handler : ((LocalPlayerAccessor) player).irisAdditions$getAmbientSoundHandlers()) {
            if (handler instanceof Source source) return source.irisAdditions$getConstantMood();
        }
        return 0.0f;
    }
}
*///?}
