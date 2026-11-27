package com.spaceagle17.irisadditions.mixin;

import java.util.ArrayList;
import java.util.List;

// Mixins that only exist on some versions or loaders. All others go in iris_additions.mixins.json
public final class VersionedMixins {
    private VersionedMixins() {}

    public static List<String> list() {
        List<String> mixins = new ArrayList<>();
        // Use the same condition as on the mixin class, and give it @SuppressWarnings({"unused", "UnusedMixin"})
        //? if <1.20.5 {
        /*mixins.add("features.hideGUIKeybind.ShaderPackScreenMixin");
        *///?}
        //? if >=1.21.2 <1.21.6 {
        /*mixins.add("fixes.hideGUIButtonFadeFix.ShaderPackOptionListMixin");
        *///?}
        return mixins;
    }
}
