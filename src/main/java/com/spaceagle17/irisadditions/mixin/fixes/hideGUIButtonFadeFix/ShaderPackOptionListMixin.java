package com.spaceagle17.irisadditions.mixin.fixes.hideGUIButtonFadeFix;

//? if >=1.21.2 <1.21.6 {
/*import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.gui.element.ShaderPackOptionList;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(value = ShaderPackOptionList.class, remap = false)
public abstract class ShaderPackOptionListMixin {
    // Draw the button now and everything that is queued to not get affected by the fade
    @Inject(method = "renderListBackground(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At("HEAD"), remap = true)
    private void irisAdditions$drawPendingFirst(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (Fixes.HIDE_GUI_BUTTON_FADE.isEnabled()) {
            guiGraphics.flush();
        }
    }
}
*///?}
