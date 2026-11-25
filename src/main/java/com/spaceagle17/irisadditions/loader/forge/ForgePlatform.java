package com.spaceagle17.irisadditions.loader.forge;

//? if forge {
/*import com.spaceagle17.irisadditions.core.platform.Loader;
import com.spaceagle17.irisadditions.core.platform.Platform;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

import java.nio.file.Path;
import java.util.Optional;

public final class ForgePlatform implements Platform {
    @Override
    public Loader loader() {
        return Loader.FORGE;
    }

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    // LoadingModList is filled before mixins are set up, unlike ModList
    @Override
    public boolean isModLoaded(String modId) {
        return LoadingModList.get().getModFileById(modId) != null;
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return Optional.ofNullable(LoadingModList.get().getModFileById(modId))
                .flatMap(file -> file.getMods().stream().filter(mod -> mod.getModId().equals(modId)).findFirst())
                .map(mod -> mod.getVersion().toString());
    }

    @Override
    public boolean isDedicatedServer() {
        return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
    }
}
*///?}
