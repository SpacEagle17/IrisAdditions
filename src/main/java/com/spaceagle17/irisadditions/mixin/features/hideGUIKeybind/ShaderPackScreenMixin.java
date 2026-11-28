package com.spaceagle17.irisadditions.mixin.features.hideGUIKeybind;

//? if <1.20.5 {
/*import com.mojang.blaze3d.platform.InputConstants;
import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin extends Screen {
    @Shadow
    private boolean guiHidden;

    private ShaderPackScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true, remap = true)
    private void irisAdditions$toggleGui(int key, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        // Iris only has the show/hide button in a world
        if (key == InputConstants.KEY_F1 && minecraft.level != null && Features.HIDE_GUI_KEYBIND.isEnabled()) {
            guiHidden = !guiHidden;
            irisAdditions$debugLog("Toggled the GUI with F1, hidden: " + guiHidden);
            init();
            // Also keeps an Iris which handles F1 itself from toggling it back
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[ShaderPackScreenMixin] " + message);
    }
}
*///?}
