package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.Versions;

import java.util.HashMap;
import java.util.Map;

public final class Feature {
    private final String id;
    private final String description;
    private final boolean alsoInEuphoriaPatcher;
    // Mod id to the first version which has this itself
    private final Map<String, String> includedSince = new HashMap<>();
    private volatile boolean enabledInConfig = true;

    Feature(String id, String description, boolean alsoInEuphoriaPatcher) {
        this.id = id;
        this.description = description;
        this.alsoInEuphoriaPatcher = alsoInEuphoriaPatcher;
    }

    Feature includedSince(String modId, String version) {
        if (Versions.toNumber(version).isEmpty()) {
            throw new IllegalArgumentException("Not a version for " + id + ": " + version);
        }
        includedSince.put(modId, version);
        return this;
    }

    // Also the config key and the mixin sub package
    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public boolean isSuppressedByEuphoria() {
        return alsoInEuphoriaPatcher && Mods.isEuphoriaLoaded();
    }

    public boolean isAlreadyIncluded() {
        return includedSince.entrySet().stream().anyMatch(entry -> Mods.isModAtLeast(entry.getKey(), entry.getValue()));
    }

    // Another installed mod already does this
    public boolean isSuppressed() {
        return isSuppressedByEuphoria() || isAlreadyIncluded();
    }

    public boolean isEnabled() {
        return enabledInConfig && !isSuppressed();
    }

    public void setEnabledInConfig(boolean enabled) {
        this.enabledInConfig = enabled;
    }
}
