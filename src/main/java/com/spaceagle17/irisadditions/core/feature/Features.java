package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;

// New and backported features
public final class Features {
    public static final FeatureGroup GROUP = new FeatureGroup("features");

    // https://github.com/IrisShaders/Iris/commit/5b89e916bf228b202e8b8b26df6742149be3b954
    // Distant Horizons has textures since 3.2.0, but only gives access to them since 3.3.0
    public static final Feature DISTANT_HORIZONS_TEXTURES = GROUP.register("distantHorizonsTexturesSupport",
            "Lets shaders use block textures on Distant Horizons terrain. Needs Distant Horizons 3.3.0 or newer. Changing this needs the shaders to be reloaded.",
            false)
            .includedSince(Mods.IRIS, "1.11.4")
            .requiresMod(Mods.DISTANT_HORIZONS, "3.3.0")
            .definesOnlyWith("DISTANT_HORIZONS")
            // The define of newer Iris, so shaders which check it just work
            .shaderDefine("DISTANT_HORIZONS_TEXTURES");

    // borrowed from EP mod lol
    public static final Feature DROP_SETTINGS_IN_SELECTION_SCREEN = GROUP.register("dropSettingsInSelectionScreen",
            "Lets you drop a shader settings file onto the shader pack list, to import it into the active shader pack.",
            true)
            .inEuphoriaPatcherSince("1.10.0");

    // Iris has this itself since Minecraft 1.20.5 and never backported it
    //? if <1.20.5 {
    /*public static final Feature HIDE_GUI_KEYBIND = GROUP.register("hideGUIKeybind",
            "Lets F1 hide and show the GUI of the shader pack screen, like its button does.", false);
    *///?}

    // https://github.com/IrisShaders/Iris/commit/67e291c518cd3bd7b6393bb9a00c60c8f64d6e42
    // Every Iris release for 26.1 and newer has it
    //? if <=1.21.11 {
    /*public static final Feature SHORTER_HOVER_TIME = GROUP.register("shorterHoverTime",
            "Shows the description of a shader setting after hovering over it for half as long.", false)
            // Only Iris for 1.21.11 got this, older Minecraft versions have lower Iris versions
            .includedSince(Mods.IRIS, "1.10.6");
    *///?}

    // Which versions get a uniform is decided per uniform, see ShaderUniforms
    public static final Feature UNIFORM_BACKPORTS = GROUP.register("uniformBackports",
            "Adds shader uniforms of newer Iris versions. Changing this needs the shaders to be reloaded.", false);

    private Features() {}
}
