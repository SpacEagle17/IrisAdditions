package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeaturesTest {
    // The registry is global
    private static final Feature SHARED = Features.register("testShared", "Also in Euphoria Patcher", true, true);
    private static final Feature OWN = Features.register("testOwn", "Only in this mod", true, false);

    @BeforeEach
    void enableAll() {
        SHARED.setEnabledInConfig(true);
        OWN.setEnabledInConfig(true);
    }

    @AfterEach
    void clearPlatform() {
        Mods.setPlatform(null);
    }

    @Test
    void everythingRunsWithoutEuphoria() {
        Mods.setPlatform(new FakePlatform("iris"));

        assertTrue(SHARED.isEnabled());
        assertTrue(OWN.isEnabled());
        assertTrue(Features.allowsMixin("testShared.SomeMixin"));
    }

    @Test
    void euphoriaTakesOverTheFeaturesItAlsoHas() {
        Mods.setPlatform(new FakePlatform("iris", "euphoria_patcher"));

        assertFalse(SHARED.isEnabled());
        assertTrue(OWN.isEnabled());
        assertFalse(Features.allowsMixin("testShared.SomeMixin"));
        assertTrue(Features.allowsMixin("testOwn.SomeMixin"));
    }

    @Test
    void configToggleDisablesAFeatureButKeepsItsMixins() {
        Mods.setPlatform(new FakePlatform("iris"));
        OWN.setEnabledInConfig(false);

        assertFalse(OWN.isEnabled());
        assertTrue(Features.allowsMixin("testOwn.SomeMixin"));
    }

    @Test
    void mixinsOutsideAFeaturePackageAreAlwaysAllowed() {
        Mods.setPlatform(new FakePlatform("euphoria_patcher"));

        assertTrue(Features.allowsMixin("ShaderPackScreenMixin"));
        assertTrue(Features.allowsMixin("notAFeature.SomeMixin"));
    }

    @Test
    void rejectsDuplicateAndUnusableIds() {
        assertThrows(IllegalStateException.class, () -> Features.register("testOwn", "Again", true, false));
        assertThrows(IllegalArgumentException.class, () -> Features.register("Bad.Id", "Bad", true, false));
    }
}
