package com.spaceagle17.irisadditions.core.feature;

// New and backported features
public final class Features {
    public static final FeatureGroup GROUP = new FeatureGroup("features");

    // Iris has this itself since Minecraft 1.20.5 and never backported it
    //? if <1.20.5 {
    /*public static final Feature HIDE_GUI_KEYBIND = GROUP.register("hideGUIKeybind",
            "Lets F1 hide and show the GUI of the shader pack screen, like its button does.",
            /^ alsoInEuphoriaPatcher ^/ false);
    *///?}

    private Features() {}
}
