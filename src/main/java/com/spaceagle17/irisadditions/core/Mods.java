package com.spaceagle17.irisadditions.core;

import com.spaceagle17.irisadditions.core.platform.Platform;

import java.util.Optional;

public final class Mods {
    public static final String EUPHORIA_PATCHER = "euphoria_patcher";

    private static volatile Platform platform;

    private Mods() {}

    public static void setPlatform(Platform implementation) {
        platform = implementation;
    }

    public static boolean isModLoaded(String modId) {
        return platform().isModLoaded(modId);
    }

    public static Optional<String> getModVersion(String modId) {
        return platform().modVersion(modId);
    }

    public static boolean isEuphoriaLoaded() {
        return isModLoaded(EUPHORIA_PATCHER);
    }

    private static Platform platform() {
        Platform current = platform;
        if (current == null) {
            throw new IllegalStateException("[IrisAdditions] Mods used before Mods.setPlatform()");
        }
        return current;
    }
}
