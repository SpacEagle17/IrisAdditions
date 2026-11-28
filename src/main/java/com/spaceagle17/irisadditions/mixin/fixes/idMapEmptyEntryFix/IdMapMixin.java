package com.spaceagle17.irisadditions.mixin.fixes.idMapEmptyEntryFix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.shaderpack.IdMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Properties;
import java.util.function.BiConsumer;

// https://github.com/IrisShaders/Iris/pull/3413
@Mixin(value = IdMap.class, remap = false)
public abstract class IdMapMixin {
    @WrapOperation(
            method = {"parseIdMap", "parseRenderTypeMap"},
            at = @At(value = "INVOKE", target = "Ljava/util/Properties;forEach(Ljava/util/function/BiConsumer;)V")
    )
    private static void irisAdditions$skipEmptyValues(Properties properties, BiConsumer<Object, Object> action,
                                                      Operation<Void> original) {
        if (!Fixes.ID_MAP_EMPTY_ENTRY.isEnabled()) {
            original.call(properties, action);
            return;
        }
        BiConsumer<Object, Object> skippingEmpty = (key, value) -> {
            String stripped = ((String) value).strip();
            // " " so during the split we have nothing afterwards
            action.accept(key, stripped.isEmpty() ? " " : stripped);
        };
        original.call(properties, skippingEmpty);
    }
}
