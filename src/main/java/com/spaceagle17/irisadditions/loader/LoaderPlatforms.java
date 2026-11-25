package com.spaceagle17.irisadditions.loader;

import com.spaceagle17.irisadditions.core.platform.Platform;

public final class LoaderPlatforms {
    private LoaderPlatforms() {}

    public static Platform create() {
        //? if fabric {
        return new com.spaceagle17.irisadditions.loader.fabric.FabricPlatform();
        //?} else if forge {
        /*return new com.spaceagle17.irisadditions.loader.forge.ForgePlatform();
        *///?} else {
        /*return new com.spaceagle17.irisadditions.loader.neoforge.NeoForgePlatform();
        *///?}
    }
}
