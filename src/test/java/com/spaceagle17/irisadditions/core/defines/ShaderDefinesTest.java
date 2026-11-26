package com.spaceagle17.irisadditions.core.defines;

import com.spaceagle17.irisadditions.core.FakePlatform;
import com.spaceagle17.irisadditions.core.Mods;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShaderDefinesTest {
    @AfterEach
    void clearPlatform() {
        Mods.setPlatform(null);
    }

    @Test
    void addsBothDefines() {
        Mods.setPlatform(new FakePlatform("iris_additions"));
        List<String> defines = new ArrayList<>();

        ShaderDefines.addDefines(defines::add, (key, value) -> defines.add(key + "=" + value));

        assertEquals(List.of("IS_IRIS_ADDITIONS", "IRIS_ADDITIONS_VERSION=10000"), defines);
    }
}
