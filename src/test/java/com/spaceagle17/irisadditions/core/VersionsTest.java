package com.spaceagle17.irisadditions.core;

import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionsTest {
    @Test
    void turnsAVersionIntoAComparableNumber() {
        assertEquals(OptionalInt.of(100), Versions.toNumber("0.1.0+mc1.20.1-forge"));
        assertEquals(OptionalInt.of(10203), Versions.toNumber("1.2.3"));
        assertEquals(OptionalInt.of(21500), Versions.toNumber("2.15-beta"));
        assertEquals(OptionalInt.empty(), Versions.toNumber("unknown"));
    }

    @Test
    void readsEveryKindOfIrisVersion() {
        assertEquals(OptionalInt.of(11107), Versions.toNumber("1.11.7+mc26.3"));
        assertEquals(OptionalInt.of(10814), Versions.toNumber("1.8.14-beta.1+mc1.21.1"));
        assertEquals(OptionalInt.of(11201), Versions.toNumber("1.12.1-snapshot+mc26.3-local"));
    }
}
