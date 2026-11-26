package com.spaceagle17.irisadditions.mixin.features.hideGUIKeybind;

//? if <1.20.5 {
/*import com.mojang.blaze3d.platform.InputConstants;
import com.spaceagle17.irisadditions.core.feature.Features;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// IntelliJ only knows the mixin config files, this one is registered in VersionedMixins
@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin extends Screen {
    @Shadow
    private boolean guiHidden;

    private ShaderPackScreenMixin(Component title) {
        super(title);
    }

    // keyPressed and init() come from Minecraft, so unlike the rest they are remapped
    @Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true, remap = true)
    private void irisAdditions$toggleGui(int key, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        // Iris only has the show/hide button in a world
        if (key == InputConstants.KEY_F1 && minecraft.level != null && Features.HIDE_GUI_KEYBIND.isEnabled()) {
            guiHidden = !guiHidden;
            init();
            // Also keeps an Iris which handles F1 itself from toggling it back
            cir.setReturnValue(true);
        }
    }
}
*///?}
