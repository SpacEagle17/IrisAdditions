package com.spaceagle17.irisadditions.mixin.startup;

import com.spaceagle17.irisadditions.core.IrisAdditions;
import com.spaceagle17.irisadditions.core.Mods;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public abstract class MainMixin {
    @Inject(method = "main([Ljava/lang/String;)V", at = @At("HEAD"))
    private static void irisAdditions$init(String[] args, CallbackInfo ci) {
        IrisAdditions.init(Mods.platform());
    }
}
