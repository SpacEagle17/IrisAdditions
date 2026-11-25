package com.spaceagle17.irisadditions.mixin.defineInjection;

import com.spaceagle17.irisadditions.core.defines.ShaderDefines;
import net.irisshaders.iris.gl.shader.StandardMacros;
import net.irisshaders.iris.helpers.StringPair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;
import java.util.List;

@Mixin(value = StandardMacros.class, remap = false)
public abstract class StandardMacrosMixin {
    @Shadow
    private static void define(List<StringPair> defines, String key) {}

    @Shadow
    private static void define(List<StringPair> defines, String key, String value) {}

    // The finished list, right before Iris makes it immutable
    @ModifyArg(
            method = "createStandardEnvironmentDefines",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableList;copyOf(Ljava/util/Collection;)Lcom/google/common/collect/ImmutableList;"
            )
    )
    private static Collection<StringPair> irisAdditions$addDefines(Collection<StringPair> defines) {
        List<StringPair> list = (List<StringPair>) defines;
        ShaderDefines.addDefines(
                key -> define(list, key),
                (key, value) -> define(list, key, value)
        );
        return list;
    }
}
