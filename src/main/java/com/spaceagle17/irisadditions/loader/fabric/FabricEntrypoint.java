package com.spaceagle17.irisadditions.loader.fabric;

//? if fabric {
import com.spaceagle17.irisadditions.core.IrisAdditions;
import net.fabricmc.api.ClientModInitializer;

public final class FabricEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IrisAdditions.init(new FabricPlatform());
    }
}
//?}
