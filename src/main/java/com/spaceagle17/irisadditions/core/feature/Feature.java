package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;

public final class Feature {
    private final String id;
    private final String description;
    private final boolean enabledByDefault;
    private final boolean alsoInEuphoriaPatcher;
    private volatile boolean enabledInConfig;

    Feature(String id, String description, boolean enabledByDefault, boolean alsoInEuphoriaPatcher) {
        this.id = id;
        this.description = description;
        this.enabledByDefault = enabledByDefault;
        this.alsoInEuphoriaPatcher = alsoInEuphoriaPatcher;
        this.enabledInConfig = enabledByDefault;
    }

    // Also the config key and the mixin sub package
    public String id() {
        return id;
    }

    public String description() {
        return description;
    }

    public boolean enabledByDefault() {
        return enabledByDefault;
    }

    public boolean isSuppressedByEuphoria() {
        return alsoInEuphoriaPatcher && Mods.isEuphoriaLoaded();
    }

    public boolean isEnabled() {
        return enabledInConfig && !isSuppressedByEuphoria();
    }

    public void setEnabledInConfig(boolean enabled) {
        this.enabledInConfig = enabled;
    }
}
