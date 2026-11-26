package com.spaceagle17.irisadditions.mixin.fixes.selectedPackCenteredFix;

import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.gui.element.IrisObjectSelectionList;
import net.irisshaders.iris.gui.element.ShaderPackSelectionList;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Iris scrolls to the selected pack before adding it to the list, so scroll again once everything is added
@Mixin(value = ShaderPackSelectionList.class, remap = false)
public abstract class ShaderPackSelectionListMixin extends IrisObjectSelectionList<ShaderPackSelectionList.BaseEntry> {
    private ShaderPackSelectionListMixin(Minecraft client, int width, int height, int top, int bottom, int left,
                                         int right, int itemHeight) {
        super(client, width, height, top, bottom, left, right, itemHeight);
    }

    @Inject(method = "refresh", at = @At("TAIL"))
    private void irisAdditions$centerSelectedPack(CallbackInfo ci) {
        ShaderPackSelectionList.BaseEntry selected = getSelected();
        // Older Minecraft keeps the selection of the previous list, even if that pack is gone
        if (Fixes.SELECTED_PACK_CENTERED.isEnabled() && children().contains(selected)) {
            centerScrollOn(selected);
        }
    }
}
