package com.spaceagle17.irisadditions.core.uniform;

import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.Versions;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;

// Uniforms of newer Iris versions. H is the UniformHolder of Iris, which core/ must not know
public final class UniformBackports<H> {
    private final Map<String, Backport<H>> byName = new LinkedHashMap<>();

    private static void debugLog(String message) {
        Log.debug("[UniformBackports] " + message);
    }

    // For uniforms which no Iris for this Minecraft version has
    public void add(String name, BiConsumer<H, String> adder) {
        register(name, new Backport<>(null, adder));
    }

    // The adder gets the name, so that it always matches the define
    public void add(String name, String includedSinceIris, BiConsumer<H, String> adder) {
        if (Versions.toNumber(includedSinceIris).isEmpty()) {
            throw new IllegalArgumentException("Not a version for " + name + ": " + includedSinceIris);
        }
        register(name, new Backport<>(includedSinceIris, adder));
    }

    private void register(String name, Backport<H> backport) {
        if (byName.putIfAbsent(name, backport) != null) {
            throw new IllegalStateException("Duplicate uniform: " + name);
        }
    }

    public void addTo(H holder) {
        for (String name : neededNames()) {
            byName.get(name).adder().accept(holder, name);
            debugLog("Added uniform " + name);
        }
    }

    public List<String> defines() {
        return neededNames().stream().map(UniformBackports::define).toList();
    }

    private List<String> neededNames() {
        if (!Features.UNIFORM_BACKPORTS.isEnabled()) return List.of();
        return byName.keySet().stream().filter(name -> !byName.get(name).isInIris()).toList();
    }

    static String define(String name) {
        return "IRIS_ADDITIONS_UNIFORM_" + name.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase(Locale.ROOT);
    }

    private record Backport<H>(String includedSinceIris, BiConsumer<H, String> adder) {
        boolean isInIris() {
            return includedSinceIris != null && Mods.isModAtLeast(Mods.IRIS, includedSinceIris);
        }
    }
}
