package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.IrisAdditions;
import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.Versions;
import com.spaceagle17.irisadditions.core.feature.Feature;
import com.spaceagle17.irisadditions.core.feature.FeatureGroup;
import com.spaceagle17.irisadditions.core.feature.FeatureGroups;
import com.spaceagle17.irisadditions.core.log.Log;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class ShaderDefines {
    private static boolean injectionLogged = false;

    private ShaderDefines() {}

    private static void debugLog(String message) {
        Log.debug("[ShaderDefines] " + message);
    }

    private static void add(Consumer<String> defineKey, List<String> defines) {
        for (String define : defines) {
            defineKey.accept(define);
            debugLog("Adding " + define + " define");
        }
    }

    public static void addDefines(Consumer<String> defineKey, BiConsumer<String, String> defineKeyValue,
                                  Predicate<String> isDefinedByIris, List<String> uniformDefines) {
        addDefines(defineKey, defineKeyValue, isDefinedByIris, uniformDefines, FeatureGroups.all());
    }

    // Tests pass their own features
    static void addDefines(Consumer<String> defineKey, BiConsumer<String, String> defineKeyValue,
                           Predicate<String> isDefinedByIris, List<String> uniformDefines, List<FeatureGroup> groups) {
        try {
            defineKey.accept("IS_IRIS_ADDITIONS");
            debugLog("Adding IS_IRIS_ADDITIONS define");

            OptionalInt version = Mods.getModVersion(IrisAdditions.MOD_ID).map(Versions::toNumber)
                    .orElse(OptionalInt.empty());
            if (version.isPresent()) {
                defineKeyValue.accept("IRIS_ADDITIONS_VERSION", String.valueOf(version.getAsInt()));
                debugLog("Adding IRIS_ADDITIONS_VERSION = " + version.getAsInt() + " define");
            } else {
                debugLog("Not adding IRIS_ADDITIONS_VERSION define - version unknown");
            }

            for (FeatureGroup group : groups) {
                for (Feature feature : group.all()) {
                    if (!feature.isEnabled()) continue;
                    if (!feature.requiredIrisDefine().map(isDefinedByIris::test).orElse(true)) continue;
                    add(defineKey, feature.shaderDefines());
                    feature.shaderValues().forEach((name, value) -> {
                        // Iris or another mod may define it already
                        if (isDefinedByIris.test(name)) return;
                        defineKeyValue.accept(name, value);
                        debugLog("Adding " + name + " = " + value + " define");
                    });
                }
            }
            add(defineKey, uniformDefines);

            if (!injectionLogged) {
                injectionLogged = true;
                Log.info("Added IrisAdditions defines to Iris");
            }
        } catch (Exception e) {
            Log.error("Exception while adding defines", e);
        }
    }
}
