package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.feature.Fixes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShaderDefinesTest {
    @AfterEach
    void clearPlatform() {
        Mods.setPlatform(null);
    }

    private static List<String> addedDefines(String... irisDefines) {
        List<String> defines = new ArrayList<>();
        ShaderDefines.addDefines(defines::add, (key, value) -> defines.add(key + "=" + value),
                List.of(irisDefines)::contains, List.of());
        return defines;
    }

    private static final List<String> MOD_DEFINES = List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000");

    @Test
    void addsTheModAndFixDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions"));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000",
                "IRIS_ADDITIONS_AT_MIDBLOCK_FIX", "EUPHORIA_PATCHES_AT_MIDBLOCK_FIX"), addedDefines());
    }

    @Test
    void leavesOutTheDefinesOfAFixIrisAlreadyHas() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "1.11.3+mc26.1"));

        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void addsTheTextureDefineWhenIrisHasDistantHorizonsOn() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "1.11.3+mc26.1")
                .withMod(Mods.DISTANT_HORIZONS, "3.3.0"));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000", "DISTANT_HORIZONS_TEXTURES"),
                addedDefines("DISTANT_HORIZONS"));
        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void addsTheUniformDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "1.11.3+mc26.1"));
        List<String> defines = new ArrayList<>();

        ShaderDefines.addDefines(defines::add, (key, value) -> {}, define -> false,
                List.of("IRIS_ADDITIONS_UNIFORM_HEAVY_FOG"));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_UNIFORM_HEAVY_FOG"), defines);
    }

    // Euphoria Patcher adds its own define, so it must not be defined twice
    @Test
    void leavesTheDefinesToAnEuphoriaPatcherWhichHasTheFix() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.EUPHORIA_PATCHER, "1.10.0-r5.9-fabric"));

        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void leavesOutTheDefinesOfADisabledFix() {
        Mods.setPlatform(new FakePlatform("iris_additions"));
        Fixes.AT_MID_BLOCK_HIGH_COORDINATES.setEnabledInConfig(false);
        try {
            assertEquals(MOD_DEFINES, addedDefines());
        } finally {
            Fixes.AT_MID_BLOCK_HIGH_COORDINATES.setEnabledInConfig(true);
        }
    }
}
