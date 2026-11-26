package com.spaceagle17.irisadditions.core;

import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Versions {
    private static final Pattern VERSION = Pattern.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+))?");

    private Versions() {}

    // 0.1.0 -> 100, 1.12.1+mc26.3 -> 11201. Whatever follows the numbers, such as -beta.1, is ignored
    public static OptionalInt toNumber(String version) {
        Matcher matcher = VERSION.matcher(version);
        if (!matcher.find()) return OptionalInt.empty();
        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        int patch = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
        return OptionalInt.of(major * 10000 + minor * 100 + patch);
    }
}
