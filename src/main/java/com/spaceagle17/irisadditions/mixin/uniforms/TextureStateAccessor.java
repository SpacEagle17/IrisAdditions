package com.spaceagle17.irisadditions.mixin.uniforms;

//? if <=1.21.11 {
/*import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// The class is not public, Minecraft 1.21.5 moved it
@SuppressWarnings({"unused", "UnusedMixin"})
//? if <1.21.5 {
/^@Mixin(targets = "com.mojang.blaze3d.platform.GlStateManager$TextureState")
^///?} else {
@Mixin(targets = "com.mojang.blaze3d.opengl.GlStateManager$TextureState")
//?}
public interface TextureStateAccessor {
    @Accessor("binding")
    int irisAdditions$getBinding();
}
*///?}
