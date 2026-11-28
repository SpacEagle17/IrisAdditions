package com.spaceagle17.irisadditions.uniforms;

//? if forge {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import com.spaceagle17.irisadditions.mixin.uniforms.GameRendererInvoker;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/bc4b8c4634f2aed4e08fce87cae16ab772257938
final class SelectedBlockUniforms {
    private SelectedBlockUniforms() {}

    // Only Oculus lacks these
    static void register(UniformBackports<UniformHolder> backports) {
        backports.add("currentSelectedBlockId", (uniforms, name) ->
                uniforms.uniform1i(PER_FRAME, name, SelectedBlockUniforms::blockId));
        backports.add("currentSelectedBlockPos", (uniforms, name) ->
                uniforms.uniform3f(PER_FRAME, name, SelectedBlockUniforms::blockPos));
    }

    private static BlockPos selectedBlock() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null
                || !((GameRendererInvoker) client.gameRenderer).irisAdditions$shouldRenderBlockOutline()
                || !(client.hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }
        return hit.getBlockPos();
    }

    private static int blockId() {
        BlockPos pos = selectedBlock();
        var blockIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
        if (pos == null || blockIds == null) return 0;

        var level = Minecraft.getInstance().level;
        BlockState state = level.getBlockState(pos);
        return state.isAir() || !level.getWorldBorder().isWithinBounds(pos) ? 0 : blockIds.getInt(state);
    }

    private static Vector3f blockPos() {
        BlockPos pos = selectedBlock();
        if (pos == null) return new Vector3f(-256.0f);

        return pos.getCenter().subtract(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition()).toVector3f();
    }
}
*///?}
