package com.spaceagle17.irisadditions.uniforms;

//? if <=1.21.11 {
/*import com.spaceagle17.irisadditions.core.uniform.UniformBackports;
import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.helpers.JomlConversions;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CameraUniforms;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Vector3d;

import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_FRAME;
import static net.irisshaders.iris.gl.uniform.UniformUpdateFrequency.PER_TICK;

// Adapted from Iris by Merlin1809 (LGPL-3.0), changed for IrisAdditions in 2026. See 3rd_party_licenses/Iris-LGPL-3.0.txt
// https://github.com/IrisShaders/Iris/pull/2951
final class PlayerAndVehicleUniforms {
    private static final Vector3d ZERO = new Vector3d();

    private PlayerAndVehicleUniforms() {}

    static void register(UniformBackports<UniformHolder> backports) {
        String since = "1.10.4";

        backports.add("feetInWater", since, (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, () -> isInShallowWater(player())));
        backports.add("inSwimmingAnimation", since, (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, () -> player() != null && player().isSwimming()));
        backports.add("isElytraFlying", since, (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, () -> player() != null && player().isFallFlying()));
        backports.add("isRiding", since, (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, () -> player() != null && player().isPassenger()));
        backports.add("vehicleInWater", since, (uniforms, name) ->
                uniforms.uniform1b(PER_TICK, name, () -> isInShallowWater(vehicle())));
        backports.add("vehicleId", since, (uniforms, name) ->
                uniforms.uniform1i(PER_TICK, name, PlayerAndVehicleUniforms::vehicleId));
        backports.add("vehicleLookVector", since, (uniforms, name) ->
                uniforms.uniform3d(PER_FRAME, name, PlayerAndVehicleUniforms::vehicleLookVector));
        backports.add("relativeVehiclePosition", since, (uniforms, name) ->
                uniforms.uniform3d(PER_FRAME, name, PlayerAndVehicleUniforms::relativeVehiclePosition));
    }

    private static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }

    private static Entity vehicle() {
        LocalPlayer player = player();
        return player == null ? null : player.getVehicle();
    }

    private static boolean isInShallowWater(Entity entity) {
        return entity != null && entity.isInWater() && !entity.isUnderWater();
    }

    private static int vehicleId() {
        Entity vehicle = vehicle();
        var entityIds = WorldRenderingSettings.INSTANCE.getEntityIds();
        if (vehicle == null || entityIds == null) return 0;

        var type = EntityType.getKey(vehicle.getType());
        return entityIds.applyAsInt(new NamespacedId(type.getNamespace(), type.getPath()));
    }

    private static Vector3d vehicleLookVector() {
        Entity vehicle = vehicle();
        return vehicle == null ? ZERO : JomlConversions.fromVec3(vehicle.getForward());
    }

    private static Vector3d relativeVehiclePosition() {
        Entity vehicle = vehicle();
        if (vehicle == null) return ZERO;

        var position = vehicle.getPosition(CapturedRenderingState.INSTANCE.getTickDelta());
        return CameraUniforms.getUnshiftedCameraPosition().sub(JomlConversions.fromVec3(position));
    }
}
*///?}
