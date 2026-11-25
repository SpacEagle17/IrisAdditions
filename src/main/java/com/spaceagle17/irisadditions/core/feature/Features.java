package com.spaceagle17.irisadditions.core.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class Features {
    private static final Map<String, Feature> BY_ID = new LinkedHashMap<>();

    // Declare features here:
    // public static final Feature EXAMPLE = register("example", "What it does.\nDefault = true",
    //         /* enabledByDefault */ true, /* alsoInEuphoriaPatcher */ true);

    private Features() {}

    static Feature register(String id, String description, boolean enabledByDefault, boolean alsoInEuphoriaPatcher) {
        if (!id.matches("[a-z][a-zA-Z0-9]*")) {
            throw new IllegalArgumentException("Feature id must be usable as a Java package name: " + id);
        }
        Feature feature = new Feature(id, description, enabledByDefault, alsoInEuphoriaPatcher);
        if (BY_ID.putIfAbsent(id, feature) != null) {
            throw new IllegalStateException("Duplicate feature id: " + id);
        }
        return feature;
    }

    public static Collection<Feature> all() {
        return Collections.unmodifiableCollection(BY_ID.values());
    }

    public static Optional<Feature> byId(String id) {
        return Optional.ofNullable(BY_ID.get(id));
    }

    // Mixins in mixin.<featureId> are skipped if Euphoria Patcher has that feature.
    // The config toggle is checked at runtime instead, so it works without a restart
    public static boolean allowsMixin(String mixinName) {
        int dot = mixinName.indexOf('.');
        if (dot < 0) return true;
        return byId(mixinName.substring(0, dot)).map(feature -> !feature.isSuppressedByEuphoria()).orElse(true);
    }
}
