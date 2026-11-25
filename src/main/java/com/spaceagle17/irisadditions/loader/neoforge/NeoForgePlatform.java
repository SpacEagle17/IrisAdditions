package com.spaceagle17.irisadditions.loader.neoforge;

//? if neoforge {
/*import com.spaceagle17.irisadditions.core.platform.Loader;
import com.spaceagle17.irisadditions.core.platform.Platform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;

import java.nio.file.Path;
import java.util.Optional;

public final class NeoForgePlatform implements Platform {
    @Override
    public Loader loader() {
        return Loader.NEOFORGE;
    }

    @Override
    public Path configDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    // LoadingModList is filled before mixins are set up, unlike ModList
    private static LoadingModList mods() {
        //? if >=1.21.9 {
        return FMLLoader.getCurrent().getLoadingModList();
        //?} else {
        /^return FMLLoader.getLoadingModList();
        ^///?}
    }

    @Override
    public boolean isModLoaded(String modId) {
        return mods().getModFileById(modId) != null;
    }

    @Override
    public Optional<String> modVersion(String modId) {
        return Optional.ofNullable(mods().getModFileById(modId))
                .flatMap(file -> file.getMods().stream().filter(mod -> mod.getModId().equals(modId)).findFirst())
                .map(mod -> mod.getVersion().toString());
    }

    @Override
    public boolean isDedicatedServer() {
        //? if >=1.21.9 {
        return FMLEnvironment.getDist() == Dist.DEDICATED_SERVER;
        //?} else {
        /^return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
        ^///?}
    }
}
*///?}
