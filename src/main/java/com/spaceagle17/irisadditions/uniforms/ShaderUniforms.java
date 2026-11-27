package com.spaceagle17.irisadditions.uniforms;

import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.DynamicUniformHolder;
import net.irisshaders.iris.gl.uniform.UniformHolder;

import java.util.List;
import java.util.stream.Stream;

public final class ShaderUniforms {
    private static final UniformBackports<UniformHolder> BACKPORTS = new UniformBackports<>();
    private static final UniformBackports<DynamicUniformHolder> DYNAMIC_BACKPORTS = new UniformBackports<>();

    static {
        // Use the same condition as on the class

        //? if <1.21.6 {
        /*HeavyFogUniform.register(BACKPORTS);
        *///?}

        //? if <=1.21.11 {
        /*PlayerAndVehicleUniforms.register(BACKPORTS);
        TextureUniforms.register(DYNAMIC_BACKPORTS);
        *///?}

        //? if <26.3 {
        /*GlintAlphaUniform.register(BACKPORTS);
        *///?}

        //? if >=1.21.2 <1.21.11 {
        /*SeaLevelUniform.register(BACKPORTS);
        *///?}

        //? if forge {
        /*ConstantMoodUniform.register(BACKPORTS);
        SelectedBlockUniforms.register(BACKPORTS);
        *///?}
    }

    private ShaderUniforms() {}

    public static void addUniforms(UniformHolder uniforms) {
        BACKPORTS.addTo(uniforms);
    }

    public static void addDynamicUniforms(DynamicUniformHolder uniforms) {
        DYNAMIC_BACKPORTS.addTo(uniforms);
    }

    public static List<String> defines() {
        return Stream.concat(BACKPORTS.defines().stream(), DYNAMIC_BACKPORTS.defines().stream()).toList();
    }
}
