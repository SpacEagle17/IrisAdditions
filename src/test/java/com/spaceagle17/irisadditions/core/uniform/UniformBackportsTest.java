package com.spaceagle17.irisadditions.core.uniform;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import com.spaceagle17.irisadditions.core.feature.Features;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UniformBackportsTest {
    private static final BiConsumer<List<String>, String> ADDER = List::add;

    @AfterEach
    void reset() {
        Mods.setPlatform(null);
        Features.UNIFORM_BACKPORTS.setEnabledInConfig(true);
    }

    private static UniformBackports<List<String>> backports() {
        UniformBackports<List<String>> backports = new UniformBackports<>();
        backports.add("heavyFog", ADDER);
        backports.add("randomUniformName", "1.10.4", ADDER);
        return backports;
    }

    private static List<String> added(UniformBackports<List<String>> backports) {
        List<String> names = new ArrayList<>();
        backports.addTo(names);
        return names;
    }

    @Test
    void addsEachUniformWithItsDefine() {
        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.10.3+mc1.21.11"));

        assertEquals(List.of("heavyFog", "randomUniformName"), added(backports()));
        assertEquals(List.of("IRIS_ADDITIONS_UNIFORM_HEAVY_FOG", "IRIS_ADDITIONS_UNIFORM_RANDOM_UNIFORM_NAME"),
                backports().defines());
    }

    @Test
    void leavesOutWhatTheInstalledIrisHas() {
        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.10.4+mc1.21.11"));

        assertEquals(List.of("heavyFog"), added(backports()));
        assertEquals(List.of("IRIS_ADDITIONS_UNIFORM_HEAVY_FOG"), backports().defines());
    }

    @Test
    void addsNothingWhenTurnedOff() {
        Mods.setPlatform(new FakePlatform());
        Features.UNIFORM_BACKPORTS.setEnabledInConfig(false);

        assertEquals(List.of(), added(backports()));
        assertEquals(List.of(), backports().defines());
    }

    @Test
    void keepsUnderscoresInDefines() {
        assertEquals("IRIS_ADDITIONS_UNIFORM_IS_SNEAKING", UniformBackports.define("is_sneaking"));
    }

    @Test
    void rejectsAUniformAddedTwice() {
        assertThrows(IllegalStateException.class, () -> backports().add("heavyFog", ADDER));
    }
}
