package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeaturesTest {
    // The groups are global
    private static final Feature SHARED = Features.GROUP.register("testShared", "Also in Euphoria Patcher", true);
    private static final Feature OWN = Features.GROUP.register("testOwn", "Only in this mod", false);
    private static final Feature BACKPORT = Features.GROUP.register("testBackport", "In newer Iris", false)
            .includedSince(Mods.IRIS, "1.12.1");

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
        assertTrue(FeatureGroups.allowsMixin("features.testShared.SomeMixin"));
    }

    @Test
    void euphoriaTakesOverTheFeaturesItAlsoHas() {
        Mods.setPlatform(new FakePlatform("iris", "euphoria_patcher"));

        assertFalse(SHARED.isEnabled());
        assertTrue(OWN.isEnabled());
        assertFalse(FeatureGroups.allowsMixin("features.testShared.SomeMixin"));
        assertTrue(FeatureGroups.allowsMixin("features.testOwn.SomeMixin"));
    }

    @Test
    void runsOnVersionsWhichDoNotHaveItYet() {
        for (String version : List.of("1.7.6+mc1.20.1", "1.11.7+mc26.3", "1.12.0+mc26.3", "unknown")) {
            Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, version));

            assertTrue(BACKPORT.isEnabled(), version);
            assertTrue(FeatureGroups.allowsMixin("features.testBackport.SomeMixin"), version);
        }
    }

    @Test
    void skipsWhatTheInstalledIrisAlreadyHas() {
        for (String version : List.of("1.12.1+mc26.3", "1.12.1-snapshot+mc26.3-local", "1.13+mc26.4", "2.0.0")) {
            Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, version));

            assertFalse(BACKPORT.isEnabled(), version);
            assertFalse(FeatureGroups.allowsMixin("features.testBackport.SomeMixin"), version);
        }
    }

    @Test
    void irisVersionsDoNotApplyToOculus() {
        Mods.setPlatform(new FakePlatform().withMod(Mods.OCULUS, "1.13.0"));

        assertTrue(BACKPORT.isEnabled());
    }

    @Test
    void menuJumpFixStopsAtTheIrisVersionWhichHasIt() {
        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.11.7+mc26.3"));
        assertTrue(FeatureGroups.allowsMixin("fixes.hideGUIMenuJumpFix.ShaderPackScreenMixin"));

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.12.1+mc26.3"));
        assertFalse(FeatureGroups.allowsMixin("fixes.hideGUIMenuJumpFix.ShaderPackScreenMixin"));
    }

    @Test
    void midBlockFixStopsAtTheIrisVersionWhichHasIt() {
        String mixin = "fixes.atMidBlockHighCoordinatesFix.ExtendedDataHelperMixin";

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.11.2+mc26.2"));
        assertTrue(FeatureGroups.allowsMixin(mixin));

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.11.3+mc26.1"));
        assertFalse(FeatureGroups.allowsMixin(mixin));
    }

    @Test
    void midBlockFixIsOnlyLeftToEuphoriaVersionsWhichHaveIt() {
        String mixin = "fixes.atMidBlockHighCoordinatesFix.ExtendedDataHelperMixin";

        Mods.setPlatform(new FakePlatform("iris").withMod(Mods.EUPHORIA_PATCHER, "1.9.3-r5.8.1-fabric"));
        assertTrue(FeatureGroups.allowsMixin(mixin));

        Mods.setPlatform(new FakePlatform("iris").withMod(Mods.EUPHORIA_PATCHER, "1.10.0-r5.9-fabric"));
        assertFalse(FeatureGroups.allowsMixin(mixin));
    }

    @Test
    void dhTexturesNeedANewEnoughDistantHorizons() {
        String mixin = "features.distantHorizonsTexturesSupport.IrisLodRenderProgramMixin";

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.7.6+mc1.20.1"));
        assertFalse(FeatureGroups.allowsMixin(mixin));

        for (String version : List.of("2.4.5-b", "3.2.0-b", "unknown")) {
            Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.7.6+mc1.20.1")
                    .withMod(Mods.DISTANT_HORIZONS, version));
            assertFalse(FeatureGroups.allowsMixin(mixin), version);
        }

        for (String version : List.of("3.3.0", "3.3.4", "3.3.5-dev", "4.0.0")) {
            Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.7.6+mc1.20.1")
                    .withMod(Mods.DISTANT_HORIZONS, version));
            assertTrue(FeatureGroups.allowsMixin(mixin), version);
        }
    }

    @Test
    void dhTexturesStopAtTheIrisVersionWhichHasThem() {
        String mixin = "features.distantHorizonsTexturesSupport.DHTerrainTransformerMixin";

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.11.3+mc26.1")
                .withMod(Mods.DISTANT_HORIZONS, "3.3.0"));
        assertTrue(FeatureGroups.allowsMixin(mixin));

        Mods.setPlatform(new FakePlatform().withMod(Mods.IRIS, "1.11.4+mc26.1")
                .withMod(Mods.DISTANT_HORIZONS, "3.3.0"));
        assertFalse(FeatureGroups.allowsMixin(mixin));
    }

    @Test
    void configToggleDisablesAFeatureButKeepsItsMixins() {
        Mods.setPlatform(new FakePlatform("iris"));
        OWN.setEnabledInConfig(false);

        assertFalse(OWN.isEnabled());
        assertTrue(FeatureGroups.allowsMixin("features.testOwn.SomeMixin"));
    }

    @Test
    void mixinsOutsideAFeaturePackageAreAlwaysAllowed() {
        Mods.setPlatform(new FakePlatform("euphoria_patcher"));

        assertTrue(FeatureGroups.allowsMixin("ShaderPackScreenMixin"));
        assertTrue(FeatureGroups.allowsMixin("defineInjection.SomeMixin"));
        assertTrue(FeatureGroups.allowsMixin("features.notAFeature.SomeMixin"));
        assertTrue(FeatureGroups.allowsMixin("fixes.testShared.SomeMixin"));
    }

    @Test
    void rejectsDuplicateAndUnusableIds() {
        assertThrows(IllegalStateException.class, () -> Features.GROUP.register("testOwn", "Again", false));
        assertThrows(IllegalArgumentException.class, () -> Features.GROUP.register("Bad.Id", "Bad", false));
    }
}
