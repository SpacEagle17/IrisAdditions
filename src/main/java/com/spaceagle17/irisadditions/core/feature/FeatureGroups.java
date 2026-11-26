package com.spaceagle17.irisadditions.core.feature;

import java.util.List;

public final class FeatureGroups {
    private FeatureGroups() {}

    public static List<FeatureGroup> all() {
        return List.of(Features.GROUP, Fixes.GROUP);
    }

    // Mixins in mixin.<groupId>.<featureId> are skipped if Iris or Euphoria Patcher already have that feature.
    // The config toggle is checked at runtime instead, so it works without a restart
    public static boolean allowsMixin(String mixinName) {
        String[] parts = mixinName.split("\\.");
        if (parts.length < 3) return true;
        return all().stream()
                .filter(group -> group.id().equals(parts[0]))
                .flatMap(group -> group.byId(parts[1]).stream())
                .noneMatch(Feature::isSuppressed);
    }
}
