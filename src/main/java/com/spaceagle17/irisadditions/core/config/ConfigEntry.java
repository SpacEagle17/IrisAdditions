package com.spaceagle17.irisadditions.core.config;

record ConfigEntry(String category, String key, Object defaultValue, String description) {
    ConfigEntry {
        if (!(defaultValue instanceof Boolean || defaultValue instanceof Integer
                || defaultValue instanceof Double || defaultValue instanceof String)) {
            throw new IllegalArgumentException("Unsupported config type for " + category + "." + key);
        }
    }

    String fullKey() {
        return category + "." + key;
    }
}
