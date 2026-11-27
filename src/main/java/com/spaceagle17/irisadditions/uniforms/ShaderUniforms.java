package com.spaceagle17.irisadditions.uniforms;

import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.UniformHolder;

import java.util.List;

public final class ShaderUniforms {
    private static final UniformBackports<UniformHolder> BACKPORTS = new UniformBackports<>();

    static {
        // Use the same condition as on the class
        //? if <1.21.6 {
        /*HeavyFogUniform.register(BACKPORTS);
        *///?}
    }

    private ShaderUniforms() {}

    public static void addUniforms(UniformHolder uniforms) {
        BACKPORTS.addTo(uniforms);
    }

    public static List<String> defines() {
        return BACKPORTS.defines();
    }
}
