package com.spaceagle17.irisadditions.core;

import com.spaceagle17.irisadditions.core.platform.Loader;
import com.spaceagle17.irisadditions.core.platform.Platform;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class FakePlatform implements Platform {
    private final Map<String, String> mods = new HashMap<>();

    public FakePlatform(String... modIds) {
        for (String modId : modIds) mods.put(modId, "1.0.0");
    }

    public FakePlatform withMod(String modId, String version) {
        mods.put(modId, version);
        return this;
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
        return mods.containsKey(modId);
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return Optional.ofNullable(mods.get(modId));
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }
}
