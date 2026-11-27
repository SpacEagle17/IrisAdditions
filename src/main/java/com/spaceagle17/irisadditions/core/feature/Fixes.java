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

    // https://github.com/IrisShaders/Iris/commit/17f4ba6ca7f637b6313f56fb3c42b21aae13bf38
    public static final Feature SELECTED_PACK_CENTERED = GROUP.register("selectedPackCenteredFix",
            "Shows the selected shader pack in the middle of the shader pack list when opening it.",
            false)
            // Iris on 1.21.1 fixed it in 1.8.14
            //? if <1.21.2 {
            /*.includedSince(Mods.IRIS, "1.8.14");
            *///?} else {
            .includedSince(Mods.IRIS, "1.11.2");
            //?}

    // 1.21.2-1.21.5 the button is draw late enough that it fades away
    //? if >=1.21.2 <1.21.6 {
    /*public static final Feature HIDE_GUI_BUTTON_FADE = GROUP.register("hideGUIButtonFadeFix",
            "Stops the hide GUI button from fading away in the shader settings screen.", false);
    *///?}

    // https://github.com/IrisShaders/Iris/pull/2941
    // Only wrong since Minecraft 1.21.5, and every Iris release for 26.1 and newer has the fix
    //? if >=1.21.5 <=1.21.11 {
    /*public static final Feature HORIZON_PERFORMANCE = GROUP.register("horizonPerformanceFix",
            "Fixes a big FPS loss from drawing the sky with shaders. Changing this needs the shaders to be reloaded.",
            false)
            // Each Minecraft version got the fix in another Iris version
            //? if <1.21.6 {
            /^.includedSince(Mods.IRIS, "1.8.12");
            ^///?} else if <1.21.11 {
            /^.includedSince(Mods.IRIS, "1.9.7");
            ^///?} else {
            .includedSince(Mods.IRIS, "1.10.3");
            //?}
    *///?}

    private Fixes() {}
}
