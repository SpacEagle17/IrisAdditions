package com.spaceagle17.irisadditions.core.config;

import com.spaceagle17.irisadditions.core.feature.Feature;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;

import java.nio.file.Path;
import java.util.Locale;

public final class ConfigHandler {
    // Config Options
    public static volatile boolean doDebugLogging = false;

    private ConfigHandler() {}

    public static void load(Path configFile, String modVersion) {
        Config.initialize(configFile, modVersion);
        configStuff();
        Config.startConfigWatcher(ConfigHandler::configStuff);
    }

    static void configStuff() {
        Config.setConfigCategoryOrder("features", "debug");

        // readWriteConfig(category, key, default, description), the type comes from the default value.
        // Optional: allowedValues for strings, TypeMigration.map(old, new) if an option changes its type

        for (Feature feature : Features.all()) {
            feature.setEnabledInConfig(Config.readWriteConfig("features", feature.id(), feature.enabledByDefault(),
                    feature.description()));
        }

        boolean configDebugLogging = Config.readWriteConfig("debug", "doDebugLogging", false,
                "Option that enables or disables debug logging. Alternatively, one can also set the JVM argument -DebugIrisAdditions=true/false which takes priority over this setting." +
                        "\nDefault = false");
        handleJVMArgumentDebugLogging(configDebugLogging);

        Config.regenerateConfig();
    }

    private static void handleJVMArgumentDebugLogging(boolean configDebugLogging) {
        // JVM argument -DebugIrisAdditions=true/false, takes priority over the config
        String argument = System.getProperty("ebugIrisAdditions");
        String normalized = argument == null ? null : argument.trim().toLowerCase(Locale.ROOT);
        boolean valid = "true".equals(normalized) || "false".equals(normalized);

        doDebugLogging = valid ? Boolean.parseBoolean(normalized) : configDebugLogging;
        Log.setDebugEnabled(doDebugLogging);

        if (argument != null && !valid) {
            Log.warn("Invalid value for -DebugIrisAdditions: " + argument + ". Only 'true' or 'false' are accepted. Using config value.");
        }
    }
}
