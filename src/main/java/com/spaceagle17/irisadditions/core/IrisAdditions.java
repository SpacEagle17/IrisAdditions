package com.spaceagle17.irisadditions.core;

import com.spaceagle17.irisadditions.core.config.Config;
import com.spaceagle17.irisadditions.core.config.ConfigHandler;
import com.spaceagle17.irisadditions.core.feature.Feature;
import com.spaceagle17.irisadditions.core.feature.FeatureGroup;
import com.spaceagle17.irisadditions.core.feature.FeatureGroups;
import com.spaceagle17.irisadditions.core.log.Log;
import com.spaceagle17.irisadditions.core.platform.Platform;

public final class IrisAdditions {
    public static final String MOD_ID = "iris_additions";
    public static final String CONFIG_FILE_NAME = "irisAdditionsSettings.toml";

    private static boolean initialized;

    private IrisAdditions() {}

    private static void debugLog(String message) {
        Log.debug("[IrisAdditions] " + message);
    }

    public static synchronized void init(Platform platform) {
        if (initialized) return;
        initialized = true;

        Mods.setPlatform(platform);
        if (platform.isDedicatedServer()) {
            Log.warn("Server detected. Disabling client-only features.");
            return;
        }

        String version = Mods.getModVersion(MOD_ID).orElse("unknown");
        ConfigHandler.load(platform.configDirectory().resolve(CONFIG_FILE_NAME), version);
        Runtime.getRuntime().addShutdownHook(new Thread(Config::stopConfigWatcher));

        for (FeatureGroup group : FeatureGroups.all()) {
            for (Feature feature : group.all()) {
                if (feature.isSuppressedByEuphoria()) {
                    debugLog("'" + group.id() + "." + feature.id() + "' is left to Euphoria Patcher");
                } else if (feature.isAlreadyIncluded()) {
                    debugLog("'" + group.id() + "." + feature.id() + "' is already part of the installed Iris");
                }
            }
        }
        Log.info("IrisAdditions v" + version + " initialized successfully on " + platform.loader() + ".");
    }
}
