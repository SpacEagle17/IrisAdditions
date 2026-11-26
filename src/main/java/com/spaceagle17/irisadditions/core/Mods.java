package com.spaceagle17.irisadditions.core;

import com.spaceagle17.irisadditions.core.platform.Platform;

import java.util.Optional;
import java.util.OptionalInt;

public final class Mods {
    public static final String EUPHORIA_PATCHER = "euphoria_patcher";
    // On Forge it is Oculus instead, which has version numbers of its own
    public static final String IRIS = "iris";
    public static final String OCULUS = "oculus";

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

    // False if the mod is missing or its version cannot be read
    public static boolean isModAtLeast(String modId, String minimumVersion) {
        OptionalInt installed = getModVersion(modId).map(Versions::toNumber).orElse(OptionalInt.empty());
        return installed.isPresent() && installed.getAsInt() >= Versions.toNumber(minimumVersion).orElseThrow();
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
