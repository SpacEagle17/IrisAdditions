package com.spaceagle17.irisadditions.mixin;

import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;
import com.spaceagle17.irisadditions.loader.LoaderPlatforms;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class IrisAdditionsMixinPlugin implements IMixinConfigPlugin {
    // Class check instead of mod id, so forks of Iris work too
    private static final String IRIS_CLASS = "net/irisshaders/iris/Iris.class";

    private String mixinPackage;
    private boolean irisPresent;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;
        this.irisPresent = getClass().getClassLoader().getResource(IRIS_CLASS) != null;
        Mods.setPlatform(LoaderPlatforms.create());
        if (!irisPresent) {
            Log.warn("Iris is not installed! Disabling IrisAdditions functionality...");
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return irisPresent && Features.allowsMixin(mixinClassName.substring(mixinPackage.length() + 1));
    }

    @Override
    public List<String> getMixins() {
        return VersionedMixins.list();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
