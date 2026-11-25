package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShaderDefinesTest {
    @AfterEach
    void clearPlatform() {
        Mods.setPlatform(null);
    }

    @Test
    void formatsTheVersionAsADecimalNumber() {
        assertEquals(Optional.of("100"), ShaderDefines.formatVersion("0.1.0+mc1.20.1-forge"));
        assertEquals(Optional.of("10203"), ShaderDefines.formatVersion("1.2.3"));
        assertEquals(Optional.of("21500"), ShaderDefines.formatVersion("2.15-beta"));
        assertEquals(Optional.empty(), ShaderDefines.formatVersion("unknown"));
    }

    @Test
    void addsBothDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions"));
        List<String> defines = new ArrayList<>();

        ShaderDefines.addDefines(defines::add, (key, value) -> defines.add(key + "=" + value));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000"), defines);
    }
}
