package com.spaceagle17.irisadditions.mixin.fixes.hideGUIMenuJumpFix;

import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Iris replaces the navigation controller on every apply, which forgets the open sub screen
@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin {
    @Shadow
    private NavigationController navigation;

    @Unique
    private NavigationController irisAdditions$previousNavigation;

    @Inject(method = "refreshForChangedPack", at = @At("HEAD"))
    private void irisAdditions$rememberNavigation(CallbackInfo ci) {
        irisAdditions$previousNavigation = navigation;
    }

    @Inject(method = "refreshForChangedPack", at = @At("TAIL"))
    private void irisAdditions$restoreNavigation(CallbackInfo ci) {
        // Null means that there is no pack anymore
        if (Fixes.HIDE_GUI_MENU_JUMP.isEnabled() && irisAdditions$previousNavigation != null && navigation != null) {
            navigation = irisAdditions$previousNavigation;
        }
        irisAdditions$previousNavigation = null;
    }

    // Iris calls this only when switching packs, and another pack has other screens
    @Inject(
            method = "applyChanges",
            at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/Iris;clearShaderPackOptionQueue()V")
    )
    private void irisAdditions$resetNavigation(CallbackInfo ci) {
        if (Fixes.HIDE_GUI_MENU_JUMP.isEnabled()) {
            navigation = null;
        }
    }
}
