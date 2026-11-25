package com.spaceagle17.irisadditions.core.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigTest {
    @TempDir
    Path directory;

    private Path file;

    @BeforeEach
    void setUp() {
        Config.reset();
        file = directory.resolve("nested").resolve("settings.toml");
    }

    @AfterEach
    void tearDown() {
        Config.reset();
    }

    private void write(String... lines) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, java.util.List.of(lines));
    }

    private void restart() {
        Config.reset();
        Config.initialize(file, "test");
    }

    @Test
    void createsTheFileWithDefaultsAndComments() throws IOException {
        Config.initialize(file, "1.2.3");
        assertEquals(5, Config.readWriteConfig("behavior", "amount", 5, "How many.\nDefault = 5"));
        Config.regenerateConfig();

        String text = Files.readString(file);
        assertTrue(text.contains("# Made for version 1.2.3"));
        assertTrue(text.contains("[behavior]"));
        assertTrue(text.contains("\t# How many."));
        assertTrue(text.contains("\tamount = 5"));
    }

    @Test
    void readsUserValuesOfEveryType() throws IOException {
        write("[a]", "flag = false", "count = 7", "ratio = 2.5", "name = \"hello\"");
        Config.initialize(file, "test");

        assertFalse(Config.readWriteConfig("a", "flag", true, "d"));
        assertEquals(7, Config.readWriteConfig("a", "count", 1, "d"));
        assertEquals(2.5, Config.readWriteConfig("a", "ratio", 1.0, "d"));
        assertEquals("hello", Config.readWriteConfig("a", "name", "x", "d"));
    }

    @Test
    void stringsSurviveARoundTrip() {
        String[] samples = {"C:\\new\\folder", "say \"hi\"", "two\nlines", "5", "true", "trailing\\"};
        for (int i = 0; i < samples.length; i++) {
            Config.reset();
            Path own = directory.resolve("roundtrip" + i + ".toml");
            Config.initialize(own, "test");
            Config.readWriteConfig("a", "text", samples[i], "d");
            Config.regenerateConfig();

            Config.reset();
            Config.initialize(own, "test");
            assertEquals(samples[i], Config.readWriteConfig("a", "text", "other", "d"));
        }
    }

    @Test
    void acceptsQuotedBooleansAndWholeNumbersForDecimals() throws IOException {
        write("[a]", "flag = \"false\"", "ratio = 3");
        Config.initialize(file, "test");

        assertFalse(Config.readWriteConfig("a", "flag", true, "d"));
        assertEquals(3.0, Config.readWriteConfig("a", "ratio", 1.0, "d"));
    }

    @Test
    void fallsBackToTheDefaultOnATypeMismatch() throws IOException {
        write("[a]", "count = many");
        Config.initialize(file, "test");

        assertEquals(4, Config.readWriteConfig("a", "count", 4, "d"));
    }

    @Test
    void migratesAChangedType() throws IOException {
        write("[a]", "mode = false");
        Config.initialize(file, "test");

        assertEquals("none", Config.readWriteConfig("a", "mode", "auto", "d",
                Config.TypeMigration.map(false, "none")));
    }

    @Test
    void limitsStringsToTheAllowedValues() throws IOException {
        write("[a]", "mode = \"NONE\"", "other = \"nonsense\"");
        Config.initialize(file, "test");
        String[] allowed = {"auto", "none"};

        assertEquals("none", Config.readWriteConfig("a", "mode", "auto", "d", allowed));
        assertEquals("auto", Config.readWriteConfig("a", "other", "auto", "d", allowed));
    }

    @Test
    void ordersCategoriesAndKeepsUnlistedOnes() throws IOException {
        Config.initialize(file, "test");
        Config.setConfigCategoryOrder("first", "empty", "second");
        Config.readWriteConfig("unlisted", "c", 1, "d");
        Config.readWriteConfig("second", "b", 1, "d");
        Config.readWriteConfig("first", "a", 1, "d");
        Config.regenerateConfig();

        String text = Files.readString(file);
        assertTrue(text.indexOf("[first]") < text.indexOf("[second]"));
        assertTrue(text.indexOf("[second]") < text.indexOf("[unlisted]"));
        assertFalse(text.contains("[empty]"));
    }

    @Test
    void reloadsAfterAnExternalEdit() throws IOException {
        Config.initialize(file, "test");
        Config.readWriteConfig("a", "count", 1, "d");
        Config.regenerateConfig();
        AtomicInteger changes = new AtomicInteger();

        Config.reloadIfChanged(changes::incrementAndGet);
        assertEquals(0, changes.get());

        write("[a]", "count = 9");
        Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis() + 5_000));
        Config.reloadIfChanged(changes::incrementAndGet);

        assertEquals(1, changes.get());
        assertEquals(9, Config.readWriteConfig("a", "count", 1, "d"));
    }

    @Test
    void handlerWritesAndAppliesTheModOptions() throws IOException {
        write("[debug]", "\tdoDebugLogging = true");
        restart();
        ConfigHandler.configStuff();

        assertTrue(ConfigHandler.doDebugLogging);
        assertTrue(Files.readString(file).contains("doDebugLogging = true"));
        ConfigHandler.doDebugLogging = false;
        com.spaceagle17.irisadditions.core.log.Log.setDebugEnabled(false);
    }
}
