package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;

// Bug fixes
public final class Fixes {
    public static final FeatureGroup GROUP = new FeatureGroup("fixes");

    // https://github.com/IrisShaders/Iris/commit/f127f610efa0a50f8f6fca45e608478e7286f85c
    public static final Feature HIDE_GUI_MENU_JUMP = GROUP.register("hideGUIMenuJumpFix",
            "Stops the shader settings from jumping back to the main screen when hiding the GUI after applying changes.",
            /* alsoInEuphoriaPatcher */ false)
            .includedSince(Mods.IRIS, "1.12.1");

    private Fixes() {}
}
