package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.Versions;
import com.spaceagle17.irisadditions.core.platform.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Feature {
    private final String id;
    private final String description;
    private final boolean alsoInEuphoriaPatcher;
    // Mod id to the first version which has this itself
    private final Map<String, String> includedSince = new HashMap<>();
    // Mod id to the first version this works with, the mod also has to be installed
    private final Map<String, String> requiredMods = new HashMap<>();
    // Null if every Euphoria Patcher version has it
    private String euphoriaPatcherSince;
    private final List<String> shaderDefines = new ArrayList<>();
    // Name to value, for defines which have one
    private final Map<String, String> shaderValues = new LinkedHashMap<>();
    // Null if the shader defines do not depend on one of Iris
    private String requiredIrisDefine;
    private boolean needsRestart;
    private volatile boolean configRead;
    private volatile boolean enabledInConfig = true;
    private volatile boolean latestInConfig;
    private volatile ModLookup modLookup;

    Feature(String id, String description, boolean alsoInEuphoriaPatcher) {
        this.id = id;
        this.description = description;
        this.alsoInEuphoriaPatcher = alsoInEuphoriaPatcher;
    }

    Feature includedSince(String modId, String version) {
        includedSince.put(modId, requireVersion(version));
        return this;
    }

    Feature requiresMod(String modId, String minimumVersion) {
        requiredMods.put(modId, requireVersion(minimumVersion));
        return this;
    }

    Feature inEuphoriaPatcherSince(String version) {
        if (!alsoInEuphoriaPatcher) {
            throw new IllegalStateException(id + " is not marked as part of Euphoria Patcher");
        }
        euphoriaPatcherSince = requireVersion(version);
        return this;
    }

    // Shaders get this define while the feature is enabled
    Feature shaderDefine(String name) {
        shaderDefines.add(name);
        return this;
    }

    Feature shaderDefine(String name, String value) {
        shaderValues.put(name, value);
        return this;
    }

    // The shader defines are only added when Iris adds this one
    Feature definesOnlyWith(String irisDefine) {
        requiredIrisDefine = irisDefine;
        return this;
    }

    // For changes which cannot be undone while the game runs, the config is only read once
    Feature needsRestart() {
        needsRestart = true;
        return this;
    }

    private String requireVersion(String version) {
        if (Versions.toNumber(version).isEmpty()) {
            throw new IllegalArgumentException("Not a version for " + id + ": " + version);
        }
        return version;
    }

    // Also the config key and the mixin sub package
    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public List<String> shaderDefines() {
        return Collections.unmodifiableList(shaderDefines);
    }

    public Map<String, String> shaderValues() {
        return Collections.unmodifiableMap(shaderValues);
    }

    public Optional<String> requiredIrisDefine() {
        return Optional.ofNullable(requiredIrisDefine);
    }

    public boolean isSuppressedByEuphoria() {
        if (!alsoInEuphoriaPatcher || !Mods.isEuphoriaLoaded()) return false;
        return euphoriaPatcherSince == null || Mods.isModAtLeast(Mods.EUPHORIA_PATCHER, euphoriaPatcherSince);
    }

    public boolean isAlreadyIncluded() {
        return includedSince.entrySet().stream().anyMatch(entry -> Mods.isModAtLeast(entry.getKey(), entry.getValue()));
    }

    public boolean isMissingRequirement() {
        return requiredMods.entrySet().stream().anyMatch(entry -> !Mods.isModAtLeast(entry.getKey(), entry.getValue()));
    }

    // Another installed mod already does this, or one this needs is missing or too old
    public boolean isSuppressed() {
        Platform platform = Mods.platform();
        ModLookup lookup = modLookup;
        if (lookup == null || lookup.platform() != platform) {
            lookup = new ModLookup(platform,
                    isSuppressedByEuphoria() || isAlreadyIncluded() || isMissingRequirement());
            modLookup = lookup;
        }
        return lookup.suppressed();
    }

    public boolean isEnabled() {
        return enabledInConfig && !isSuppressed();
    }

    // True if this is a new change which only applies after a restart
    public boolean setEnabledInConfig(boolean enabled) {
        if (needsRestart && configRead) {
            boolean changed = enabled != latestInConfig;
            latestInConfig = enabled;
            return changed && enabled != enabledInConfig;
        }
        configRead = true;
        latestInConfig = enabled;
        this.enabledInConfig = enabled;
        return false;
    }

    private record ModLookup(Platform platform, boolean suppressed) {}
}
