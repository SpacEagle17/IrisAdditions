package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;

// Bug fixes
public final class Fixes {
    public static final FeatureGroup GROUP = new FeatureGroup("fixes");

    // https://github.com/IrisShaders/Iris/commit/f127f610efa0a50f8f6fca45e608478e7286f85c
    public static final Feature HIDE_GUI_MENU_JUMP = GROUP.register("hideGUIMenuJumpFix",
            "Stops the shader settings from jumping back to the main screen when hiding the GUI after applying changes.",
            false)
            .includedSince(Mods.IRIS, "1.12.1");

    // Thanks to GeForceLegend for finding this bug fix! https://github.com/IrisShaders/Iris/pull/3246
    public static final Feature AT_MID_BLOCK_HIGH_COORDINATES = GROUP.register("atMidBlockHighCoordinatesFix",
            "Fixes the shader attribute at_midBlock breaking at high coordinates. Changing this needs the shaders and chunks to be reloaded.",
            true)
            .inEuphoriaPatcherSince("1.10.0")
            .includedSince(Mods.IRIS, "1.11.3")
            .shaderDefine("IRIS_ADDITIONS_AT_MIDBLOCK_FIX")
            // Complementary already checks this one. Left out of the docs on purpose, shaders should use ours
            .shaderDefine("EUPHORIA_PATCHES_AT_MIDBLOCK_FIX");

    private Fixes() {}
}
