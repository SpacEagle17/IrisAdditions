package com.spaceagle17.irisadditions.fixes;

//? if >=1.21.4 <=1.21.8 {
/*import com.spaceagle17.irisadditions.core.feature.Fixes;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

// Adapted from Iris by IMS (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/commit/c00471baff637f81e3eb0f5f028da19ab2cc3ef3
public final class SkyStageShard extends RenderStateShard {
    public static final SkyStageShard SUN = new SkyStageShard("irisadditions_sun", WorldRenderingPhase.SUN);
    public static final SkyStageShard SUNSET = new SkyStageShard("irisadditions_sunset", WorldRenderingPhase.SUNSET);
    public static final SkyStageShard MOON = new SkyStageShard("irisadditions_moon", WorldRenderingPhase.MOON);

    private SkyStageShard(String name, WorldRenderingPhase stage) {
        super(name, () -> setStage(stage), () -> setStage(WorldRenderingPhase.NONE));
    }

    private static void setStage(WorldRenderingPhase stage) {
        Iris.getPipelineManager().getPipeline().ifPresent(pipeline -> pipeline.setPhase(stage));
    }

    public RenderType wrap(RenderType type) {
        if (!Fixes.SKY_RENDER_STAGE.isEnabled()) return type;
        return new OuterWrappedRenderType(name, type, this);
    }
}
*///?}
