package com.spaceagle17.irisadditions.core;

import com.spaceagle17.irisadditions.core.platform.Loader;
import com.spaceagle17.irisadditions.core.platform.Platform;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public final class FakePlatform implements Platform {
    private final Set<String> mods;

    public FakePlatform(String... mods) {
        this.mods = Set.of(mods);
    }

    @Override
    public Loader loader() {
        return Loader.FABRIC;
    }

    @Override
    public Path configDirectory() {
        return Path.of("config");
    }

    @Override
    public boolean isModLoaded(String modId) {
        return mods.contains(modId);
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return isModLoaded(modId) ? Optional.of("1.0.0") : Optional.empty();
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }
}
