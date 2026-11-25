package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.IrisAdditions;
import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.log.Log;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ShaderDefines {
    private static final Pattern VERSION = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?");
    private static boolean injectionLogged = false;

    private ShaderDefines() {}

    private static void debugLog(String message) {
        Log.debug("[ShaderDefines] " + message);
    }

    public static void addDefines(Consumer<String> defineKey, BiConsumer<String, String> defineKeyValue) {
        try {
            defineKey.accept("IS_IRIS_ADDITIONS");
            debugLog("Adding IS_IRIS_ADDITIONS define");

            Optional<String> version = Mods.getModVersion(IrisAdditions.MOD_ID).flatMap(ShaderDefines::formatVersion);
            if (version.isPresent()) {
                defineKeyValue.accept("IRIS_ADDITIONS_VERSION", version.get());
                debugLog("Adding IRIS_ADDITIONS_VERSION = " + version.get() + " define");
            } else {
                debugLog("Not adding IRIS_ADDITIONS_VERSION define - version unknown");
            }

            if (!injectionLogged) {
                injectionLogged = true;
                Log.info("Added IrisAdditions defines to Iris");
            }
        } catch (Exception e) {
            Log.error("Exception while adding defines", e);
        }
    }

    // 0.1.0 -> 100, 1.2.3 -> 10203
    static Optional<String> formatVersion(String version) {
        Matcher matcher = VERSION.matcher(version);
        if (!matcher.find()) return Optional.empty();
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
        return Optional.of(String.valueOf(major * 10000 + minor * 100 + patch));
    }
}
