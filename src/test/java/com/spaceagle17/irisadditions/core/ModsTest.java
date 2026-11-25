package com.spaceagle17.irisadditions.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModsTest {
    @AfterEach
    void clearPlatform() {
        Mods.setPlatform(null);
    }

    @Test
    void reportsInstalledMods() {
        Mods.setPlatform(new FakePlatform("iris"));

        assertTrue(Mods.isModLoaded("iris"));
        assertFalse(Mods.isModLoaded("oculus"));
        assertFalse(Mods.isEuphoriaLoaded());
    }

    @Test
    void detectsEuphoriaPatcherByItsModId() {
        Mods.setPlatform(new FakePlatform("euphoria_patcher"));

        assertTrue(Mods.isEuphoriaLoaded());
    }
}
