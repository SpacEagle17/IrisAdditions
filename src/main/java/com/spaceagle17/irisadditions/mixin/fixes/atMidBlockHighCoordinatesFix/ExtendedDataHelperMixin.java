package com.spaceagle17.irisadditions.mixin.fixes.atMidBlockHighCoordinatesFix;

import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.vertices.ExtendedDataHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Thanks to GeForceLegend for finding this bug fix! https://github.com/IrisShaders/Iris/pull/3246
@Mixin(value = ExtendedDataHelper.class, remap = false)
public abstract class ExtendedDataHelperMixin {
    @ModifyVariable(method = "computeMidBlock", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static int irisAdditions$maskLocalPosX(int localPosX) {
        return irisAdditions$mask(localPosX);
    }

    @ModifyVariable(method = "computeMidBlock", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static int irisAdditions$maskLocalPosY(int localPosY) {
        return irisAdditions$mask(localPosY);
    }

    @ModifyVariable(method = "computeMidBlock", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private static int irisAdditions$maskLocalPosZ(int localPosZ) {
        return irisAdditions$mask(localPosZ);
    }

    @Unique
    private static int irisAdditions$mask(int position) {
        return Fixes.AT_MID_BLOCK_HIGH_COORDINATES.isEnabled() ? position & 0xFFFF : position;
    }
}
