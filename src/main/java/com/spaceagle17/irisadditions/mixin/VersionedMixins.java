package com.spaceagle17.irisadditions.mixin;

import java.util.ArrayList;
import java.util.List;

// Mixins that only exist on some versions or loaders. All others go in iris_additions.mixins.json
public final class VersionedMixins {
    private VersionedMixins() {}

    public static List<String> list() {
        List<String> mixins = new ArrayList<>();
        // Use the same condition as on the mixin class:
        // //? if <1.21.9
        // mixins.add("features.exampleFeature.OldRendererMixin");
        return mixins;
    }
}
