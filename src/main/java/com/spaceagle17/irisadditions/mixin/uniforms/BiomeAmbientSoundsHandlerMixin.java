package com.spaceagle17.irisadditions.mixin.uniforms;

//? if forge {
/*import com.llamalad7.mixinextras.sugar.Local;
import com.spaceagle17.irisadditions.uniforms.ConstantMoodUniform;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"unused", "UnusedMixin"})
@Mixin(BiomeAmbientSoundsHandler.class)
public abstract class BiomeAmbientSoundsHandlerMixin implements ConstantMoodUniform.Source {
    @Shadow
    @Final
    private LocalPlayer player;

    @Unique
    private float irisAdditions$constantMood;

    @SuppressWarnings("target")
    @Inject(
            method = "lambda$tick$3(Lnet/minecraft/world/level/biome/AmbientMoodSettings;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBrightness(Lnet/minecraft/world/level/LightLayer;Lnet/minecraft/core/BlockPos;)I",
                    ordinal = 0
            )
    )
    private void irisAdditions$updateConstantMood(AmbientMoodSettings settings, CallbackInfo ci,
                                                  @Local BlockPos pos) {
        irisAdditions$constantMood =
                ConstantMoodUniform.nextMood(irisAdditions$constantMood, player.level(), settings, pos);
    }

    @Override
    public float irisAdditions$getConstantMood() {
        return irisAdditions$constantMood;
    }
}
*///?}
