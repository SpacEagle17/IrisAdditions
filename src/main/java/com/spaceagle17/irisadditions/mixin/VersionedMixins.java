package com.spaceagle17.irisadditions.mixin;

import java.util.ArrayList;
import java.util.List;

// Mixins that only exist on some versions or loaders. All others go in iris_additions.mixins.json
public final class VersionedMixins {
    private VersionedMixins() {}

    public static List<String> list() {
        List<String> mixins = new ArrayList<>();
        // Use the same condition as on the mixin class, and give it @SuppressWarnings({"unused", "UnusedMixin"})
        // One block per condition, ordered by version and then by loader

        //? if <1.20.5 {
        /*mixins.add("features.hideGUIKeybind.ShaderPackScreenMixin");
        *///?}

        //? if <=1.21.11 {
        /*mixins.add("features.shorterHoverTime.ShaderPackScreenMixin");
        mixins.add("uniforms.IrisMixin");
        mixins.add("uniforms.PBRTextureManagerMixin");
        mixins.add("uniforms.TextureStateAccessor");
        *///?}

        //? if >=1.21.2 <1.21.6 {
        /*mixins.add("fixes.hideGUIButtonFadeFix.ShaderPackOptionListMixin");
        *///?}

        //? if >=1.21.4 <=1.21.8 {
        /*mixins.add("fixes.skyRenderStageFix.SkyRendererMixin");
        *///?}

        //? if >=1.21.5 <=1.21.11 {
        /*mixins.add("fixes.horizonPerformanceFix.HorizonRendererMixin");
        *///?}

        //? if >=1.21.9 <=26.2 {
        /*mixins.add("features.clampedEndFlashAngle.EndFlashStateMixin");
        *///?}

        //? if forge {
        /*mixins.add("uniforms.BiomeAmbientSoundsHandlerMixin");
        mixins.add("uniforms.GameRendererInvoker");
        mixins.add("uniforms.LocalPlayerAccessor");
        *///?}

        return mixins;
    }
}
