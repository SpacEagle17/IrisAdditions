package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.feature.DummyFeatures;
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
                List.of(irisDefines)::contains, List.of(), List.of(DummyFeatures.GROUP));
        return defines;
    }

    private static final List<String> MOD_DEFINES = List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000");

    @Test
    void addsTheModAndFeatureDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions"));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000",
                "TEST_BACKPORT", "TEST_BACKPORT_ALIAS"), addedDefines());
    }

    @Test
    void leavesOutTheDefinesOfAFeatureIrisAlreadyHas() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "2.0.0"));

        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void addsADefineOnlyWhenIrisHasTheOneItNeeds() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "2.0.0")
                .withMod(Mods.DISTANT_HORIZONS, "3.3.0"));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000", "TEST_DEPENDENT"),
                addedDefines("TEST_IRIS_DEFINE"));
        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void addsTheUniformDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.IRIS, "2.0.0"));
        List<String> defines = new ArrayList<>();

        ShaderDefines.addDefines(defines::add, (key, value) -> {}, define -> false,
                List.of("TEST_UNIFORM"), List.of(DummyFeatures.GROUP));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "TEST_UNIFORM"), defines);
    }

    // Euphoria Patcher adds its own define, so it must not be defined twice
    @Test
    void leavesTheDefinesToAnEuphoriaPatcherWhichHasTheFeature() {
        Mods.setPlatform(new FakePlatform("iris_additions").withMod(Mods.EUPHORIA_PATCHER, "1.10.0-r5.9-fabric"));

        assertEquals(MOD_DEFINES, addedDefines());
    }

    @Test
    void leavesOutTheDefinesOfADisabledFeature() {
        Mods.setPlatform(new FakePlatform("iris_additions"));
        DummyFeatures.BACKPORT.setEnabledInConfig(false);
        try {
            assertEquals(MOD_DEFINES, addedDefines());
        } finally {
            DummyFeatures.BACKPORT.setEnabledInConfig(true);
        }
    }
}
