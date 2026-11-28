package com.spaceagle17.irisadditions.mixin.fixes.idMapEmptyEntryFix;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.spaceagle17.irisadditions.core.feature.Fixes;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.shaderpack.IdMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

// https://github.com/IrisShaders/Iris/pull/3413
@Mixin(value = IdMap.class, remap = false)
public abstract class IdMapMixin {
    @WrapOperation(
            method = {"parseIdMap", "parseRenderTypeMap"},
            at = @At(value = "INVOKE", target = "Ljava/util/Properties;forEach(Ljava/util/function/BiConsumer;)V")
    )
    private static void irisAdditions$skipEmptyValues(Properties properties, BiConsumer<Object, Object> action,
                                                      Operation<Void> original,
                                                      @Local(argsOnly = true, ordinal = 0) String keyPrefix,
                                                      @Local(argsOnly = true, ordinal = 1) String fileName) {
        if (!Fixes.ID_MAP_EMPTY_ENTRY.isEnabled()) {
            original.call(properties, action);
            return;
        }
        AtomicInteger skipped = new AtomicInteger();
        BiConsumer<Object, Object> skippingEmpty = (key, value) -> {
            String stripped = ((String) value).strip();
            if (stripped.isEmpty() && ((String) key).startsWith(keyPrefix)) skipped.incrementAndGet();
            // " " so during the split we have nothing afterwards
            action.accept(key, stripped.isEmpty() ? " " : stripped);
        };
        original.call(properties, skippingEmpty);
        if (skipped.get() > 0) {
            irisAdditions$debugLog("Skipped " + skipped.get() + " empty '" + keyPrefix + "' lines in " + fileName);
        }
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[IdMapMixin] " + message);
    }
}
