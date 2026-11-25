package com.spaceagle17.irisadditions.core.platform;

import java.nio.file.Path;
import java.util.Optional;

// Implemented per loader. Must be safe to call while mixins are being set up
public interface Platform {
    Loader loader();

    Path configDirectory();

    boolean isModLoaded(String modId);

    Optional<String> modVersion(String modId);

    boolean isDedicatedServer();
}
