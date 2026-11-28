package com.spaceagle17.irisadditions.core.feature;

import com.spaceagle17.irisadditions.core.Mods;

public final class DummyFeatures {
    public static final FeatureGroup GROUP = new FeatureGroup("dummy");

    public static final Feature BACKPORT = GROUP.register("backport", "In newer Iris and Euphoria Patcher", true)
            .includedSince(Mods.IRIS, "2.0.0")
            .inEuphoriaPatcherSince("1.10.0")
            .shaderDefine("TEST_BACKPORT")
            .shaderDefine("TEST_BACKPORT_ALIAS")
            .shaderDefine("TEST_VALUE", "32");

    public static final Feature DEPENDENT = GROUP.register("dependent", "Needs another mod", false)
            .requiresMod(Mods.DISTANT_HORIZONS, "3.3.0")
            .definesOnlyWith("TEST_IRIS_DEFINE")
            .shaderDefine("TEST_DEPENDENT");

    private DummyFeatures() {}
}
