package com.spaceagle17.irisadditions.core.config;

import com.spaceagle17.irisadditions.core.log.Log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Config {
    private static final Map<String, Object> values = new LinkedHashMap<>();
    private static final Map<String, ConfigEntry> schema = new LinkedHashMap<>();
    private static final List<String> categoryOrder = new ArrayList<>();
    private static Path path;
    private static String modVersion = "unknown";
    private static FileTime lastModified;
    private static ScheduledExecutorService scheduler;

    private Config() {}

    private static void debugLog(String message) {
        Log.debug("[Config] " + message);
    }

    public static synchronized void initialize(Path configFile, String version) {
        path = configFile;
        modVersion = version;
        load();
    }

    private static synchronized void load() {
        try {
            if (!Files.exists(path)) {
                debugLog("No config file found, will create on first save");
                return;
            }

            // Taken before reading, so an edit made while reading is picked up by the next check
            FileTime modified = Files.getLastModifiedTime(path);
            String category = "";
            for (String rawLine : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    category = line.substring(1, line.length() - 1).trim();
                } else if (line.contains("=")) {
                    int separator = line.indexOf('=');
                    String key = line.substring(0, separator).trim();
                    String value = line.substring(separator + 1).trim();
                    values.put(category.isEmpty() ? key : category + "." + key, parseValue(value));
                }
            }
            lastModified = modified;
            debugLog("Config loaded successfully");
        } catch (IOException e) {
            Log.error("Error loading config: " + e.getMessage());
        }
    }

    // Quoted values stay strings
    private static Object parseValue(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return unescape(value.substring(1, value.length() - 1));
        }
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        try {
            if (value.contains(".")) return Double.parseDouble(value);
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    // Single pass, so that an escaped backslash followed by "n" stays that way
    private static String unescape(String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '\\' && i + 1 < text.length()) {
                char next = text.charAt(i + 1);
                if (next == '\\' || next == '"' || next == 'n') {
                    result.append(next == 'n' ? '\n' : next);
                    i++;
                    continue;
                }
            }
            result.append(current);
        }
        return result.toString();
    }

    public static synchronized void setConfigCategoryOrder(String... categories) {
        categoryOrder.clear();
        categoryOrder.addAll(Arrays.asList(categories));
    }

    public static synchronized <T> T readWriteConfig(String category, String key, T defaultValue, String description,
                                                     TypeMigration... migrations) {
        ConfigEntry entry = new ConfigEntry(category, key, defaultValue, description);
        schema.put(entry.fullKey(), entry);

        Object stored = values.get(entry.fullKey());
        Object value = stored == null ? defaultValue : coerce(entry, stored, migrations);
        values.put(entry.fullKey(), value);

        @SuppressWarnings("unchecked")
        T result = (T) value;
        return result;
    }

    public static synchronized String readWriteConfig(String category, String key, String defaultValue, String description,
                                                      String[] allowedValues, TypeMigration... migrations) {
        String value = readWriteConfig(category, key, defaultValue, description, migrations);
        String result = Arrays.stream(allowedValues).filter(value::equalsIgnoreCase).findFirst().orElse(null);
        if (result == null) {
            Log.warn("Invalid value '" + value + "' for " + category + "." + key + ", using default: " + defaultValue);
            result = defaultValue;
        }
        values.put(category + "." + key, result);
        return result;
    }

    private static Object coerce(ConfigEntry entry, Object stored, TypeMigration[] migrations) {
        Object defaultValue = entry.defaultValue();
        for (TypeMigration migration : migrations) {
            if (!stored.getClass().equals(defaultValue.getClass()) && migration.matches(stored)) {
                Log.info("Migrated '" + entry.fullKey() + "' from " + stored + " to " + migration.newValue());
                return migration.newValue();
            }
        }
        if (stored.getClass().equals(defaultValue.getClass())) return stored;
        if (defaultValue instanceof Double && stored instanceof Integer number) return number.doubleValue();
        // A boolean or number the user wrapped in quotes
        if (!(defaultValue instanceof String) && stored instanceof String text) {
            Object parsed = parseValue(text.trim());
            if (!(parsed instanceof String)) return coerce(entry, parsed, migrations);
        }
        Log.warn("Type mismatch for '" + entry.fullKey() + "': " + stored + " is not a "
                + defaultValue.getClass().getSimpleName() + ", using default");
        return defaultValue;
    }

    public static synchronized void regenerateConfig() {
        List<String> lines = new ArrayList<>();
        lines.add("# This file stores configuration options for the IrisAdditions mod");
        lines.add("# Made for version " + modVersion);
        lines.add("# Thank you for using IrisAdditions - SpacEagle17");

        Set<String> categories = new LinkedHashSet<>(categoryOrder);
        schema.values().forEach(entry -> categories.add(entry.category()));

        for (String category : categories) {
            List<ConfigEntry> entries = schema.values().stream().filter(e -> e.category().equals(category)).toList();
            if (entries.isEmpty()) continue;

            lines.add("");
            lines.add("[" + category + "]");
            for (int i = 0; i < entries.size(); i++) {
                ConfigEntry entry = entries.get(i);
                if (i > 0) lines.add("");
                for (String comment : entry.description().split("\n")) {
                    lines.add("\t# " + comment.trim());
                }
                Object value = values.getOrDefault(entry.fullKey(), entry.defaultValue());
                String text = value instanceof String string ? "\"" + escape(string) + "\"" : value.toString();
                lines.add("\t" + entry.key() + " = " + text);
            }
        }

        try {
            if (Files.exists(path) && Files.readAllLines(path, StandardCharsets.UTF_8).equals(lines)) return;
            Files.createDirectories(path.toAbsolutePath().getParent());
            Files.write(path, lines, StandardCharsets.UTF_8);
            lastModified = Files.getLastModifiedTime(path);
            debugLog("Config file written");
        } catch (IOException e) {
            Log.error("Error writing config: " + e.getMessage());
        }
    }

    public static synchronized void startConfigWatcher(Runnable onChange) {
        if (scheduler != null) return;
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "IrisAdditionsConfigWatcher");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> reloadIfChanged(onChange), 10, 10, TimeUnit.SECONDS);
    }

    static synchronized void reloadIfChanged(Runnable onChange) {
        try {
            if (Files.exists(path) && !Files.getLastModifiedTime(path).equals(lastModified)) {
                debugLog("Config file changed externally, reloading");
                load();
                onChange.run();
            }
        } catch (Exception e) {
            // An exception escaping would silently stop the scheduled task
            Log.error("Error reloading config: " + e.getMessage());
        }
    }

    public static synchronized void stopConfigWatcher() {
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    // For tests
    static synchronized void reset() {
        stopConfigWatcher();
        values.clear();
        schema.clear();
        categoryOrder.clear();
        path = null;
        lastModified = null;
    }

    public record TypeMigration(Object oldValue, Object newValue) {
        public static TypeMigration map(Object oldValue, Object newValue) {
            return new TypeMigration(oldValue, newValue);
        }

        boolean matches(Object value) {
            if (oldValue instanceof String old && value instanceof String text) return old.equalsIgnoreCase(text);
            return oldValue.equals(value);
        }
    }
}
