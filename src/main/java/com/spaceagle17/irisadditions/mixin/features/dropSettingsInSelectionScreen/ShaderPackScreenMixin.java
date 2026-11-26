package com.spaceagle17.irisadditions.mixin.features.dropSettingsInSelectionScreen;

import com.spaceagle17.irisadditions.core.feature.Features;
import com.spaceagle17.irisadditions.core.log.Log;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

// Iris only imports settings files in the settings screen, the pack list rejects them
@Mixin(value = ShaderPackScreen.class, remap = false)
public abstract class ShaderPackScreenMixin {
    @Shadow
    public abstract void importPackOptions(Path settingFile);

    @Inject(method = "onPackListFilesDrop", at = @At("HEAD"), cancellable = true)
    private void irisAdditions$importDroppedSettings(List<Path> paths, CallbackInfo ci) {
        if (!Features.DROP_SETTINGS_IN_SELECTION_SCREEN.isEnabled() || paths.size() != 1) return;

        Path file = paths.get(0);
        // Without an active pack there is nothing to import into, so Iris can show its own error
        if (!file.toString().toLowerCase(Locale.ROOT).endsWith(".txt") || !Files.isRegularFile(file)
                || Iris.getCurrentPack().isEmpty()) {
            return;
        }

        irisAdditions$debugLog("Importing dropped settings file: " + file.getFileName());
        importPackOptions(file);
        ci.cancel();
    }

    @Unique
    private static void irisAdditions$debugLog(String message) {
        Log.debug("[ShaderPackScreenMixin] " + message);
    }
}
