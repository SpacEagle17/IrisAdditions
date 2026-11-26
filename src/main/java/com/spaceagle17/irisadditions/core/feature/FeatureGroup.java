package com.spaceagle17.irisadditions.core.feature;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class FeatureGroup {
    private final String id;
    private final Map<String, Feature> byId = new LinkedHashMap<>();

    FeatureGroup(String id) {
        this.id = id;
    }

    // Also the config category and the mixin sub package
    public String id() {
        return id;
    }

    Feature register(String featureId, String description, boolean alsoInEuphoriaPatcher) {
        if (!featureId.matches("[a-z][a-zA-Z0-9]*")) {
            throw new IllegalArgumentException("Feature id must be usable as a Java package name: " + featureId);
        }
        Feature feature = new Feature(featureId, description, alsoInEuphoriaPatcher);
        if (byId.putIfAbsent(featureId, feature) != null) {
            throw new IllegalStateException("Duplicate id in " + id + ": " + featureId);
        }
        return feature;
    }

    public Collection<Feature> all() {
        return Collections.unmodifiableCollection(byId.values());
    }

    public Optional<Feature> byId(String featureId) {
        return Optional.ofNullable(byId.get(featureId));
    }
}
