package com.spaceagle17.irisadditions.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spaceagle17.irisadditions.core.feature.FeatureGroup;
import com.spaceagle17.irisadditions.core.feature.FeatureGroups;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// Checks every mixin against the Minecraft and Iris jars of this version, without starting the game
class MixinTargetTest {
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String PSEUDO = "Lorg/spongepowered/asm/mixin/Pseudo;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";

    private static URLClassLoader targets;
    private static List<Path> classDirectories;
    private static JsonObject config;

    @BeforeAll
    static void load() throws IOException {
        List<URL> urls = new ArrayList<>();
        for (Path path : paths("irisAdditions.test.targetClasspath")) {
            try {
                urls.add(path.toUri().toURL());
            } catch (MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }
        targets = new URLClassLoader(urls.toArray(URL[]::new), null);
        classDirectories = paths("irisAdditions.test.classes");
        Path configFile = Path.of(property("irisAdditions.test.resources"), "iris_additions.mixins.json");
        config = JsonParser.parseString(Files.readString(configFile)).getAsJsonObject();
    }

    @AfterAll
    static void close() throws IOException {
        targets.close();
    }

    private static String property(String name) {
        String value = System.getProperty(name);
        if (value == null) fail("Run this test through Gradle, " + name + " is not set");
        return value;
    }

    private static List<Path> paths(String name) {
        return Arrays.stream(property(name).split(File.pathSeparator)).map(Path::of).toList();
    }

    private static List<String> registeredMixins() {
        List<String> mixins = new ArrayList<>(VersionedMixins.list());
        for (String side : List.of("mixins", "client", "server")) {
            if (config.has(side)) config.getAsJsonArray(side).forEach(name -> mixins.add(name.getAsString()));
        }
        return mixins;
    }

    @Test
    void everyMixinClassIsRegistered() throws IOException {
        String packagePath = config.get("package").getAsString().replace('.', '/');
        List<String> compiled = new ArrayList<>();
        for (Path directory : classDirectories) {
            Path root = directory.resolve(packagePath);
            if (!Files.isDirectory(root)) continue;
            try (Stream<Path> files = Files.walk(root)) {
                for (Path file : files.filter(f -> f.toString().endsWith(".class")).toList()) {
                    ClassNode node = read(Files.readAllBytes(file));
                    if (annotation(node.invisibleAnnotations, MIXIN) == null) continue;
                    String name = root.relativize(file).toString().replace(File.separatorChar, '.');
                    compiled.add(name.substring(0, name.length() - ".class".length()));
                }
            }
        }
        List<String> registered = registeredMixins();
        assertTrue(registered.containsAll(compiled),
                "Mixins missing from iris_additions.mixins.json / VersionedMixins: "
                        + compiled.stream().filter(name -> !registered.contains(name)).toList());
        assertTrue(compiled.containsAll(registered),
                "Registered mixins without a class on this version: "
                        + registered.stream().filter(name -> !compiled.contains(name)).toList());
    }

    @Test
    void everyFeatureMixinBelongsToADeclaredFeature() {
        for (String name : registeredMixins()) {
            String[] parts = name.split("\\.");
            for (FeatureGroup group : FeatureGroups.all()) {
                if (!parts[0].equals(group.id())) continue;
                assertTrue(parts.length > 2 && group.byId(parts[1]).isPresent(),
                        name + " is not in the folder of a declared entry of " + group.id());
            }
        }
    }

    @Test
    void everyMixinTargetExists() throws IOException {
        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (String name : registeredMixins()) {
            ClassNode mixin = mixinClass(name);
            AnnotationNode annotation = annotation(mixin.invisibleAnnotations, MIXIN);
            List<String> targetNames = new ArrayList<>();
            for (Object type : values(annotation, "value")) targetNames.add(((Type) type).getInternalName());
            for (Object type : values(annotation, "targets")) targetNames.add(type.toString().replace('.', '/'));
            if (targetNames.isEmpty()) problems.add(name + " has no target");

            for (String targetName : targetNames) {
                ClassNode target = targetClass(targetName);
                if (target == null) {
                    if (annotation(mixin.invisibleAnnotations, PSEUDO) == null) {
                        problems.add(name + ": target class " + targetName + " does not exist");
                    }
                    continue;
                }
                checked++;
                checkMembers(name, mixin, target, problems);
            }
        }
        assertTrue(problems.isEmpty(), "Broken mixins:\n  " + String.join("\n  ", problems));
        assertTrue(checked > 0, "No mixin target was checked");
    }

    private static void checkMembers(String name, ClassNode mixin, ClassNode target, List<String> problems) {
        for (FieldNode field : mixin.fields) {
            if (hasAnnotation(SHADOW, field.visibleAnnotations, field.invisibleAnnotations)
                    && target.fields.stream().noneMatch(f -> f.name.equals(field.name) && f.desc.equals(field.desc))) {
                problems.add(name + ": @Shadow field " + field.name + " is not in " + target.name);
            }
        }
        for (MethodNode method : mixin.methods) {
            for (AnnotationNode annotation : concat(method.visibleAnnotations, method.invisibleAnnotations)) {
                String where = name + "." + method.name + ": ";
                if (annotation.desc.equals(SHADOW) || annotation.desc.equals(OVERWRITE)) {
                    require(target, method.name + method.desc, where, problems);
                } else if (annotation.desc.equals(ACCESSOR)) {
                    String field = memberName(annotation, method.name, "get", "set", "is");
                    if (target.fields.stream().noneMatch(f -> f.name.equals(field))) {
                        problems.add(where + "field " + field + " is not in " + target.name);
                    }
                } else if (annotation.desc.equals(INVOKER)) {
                    String invoked = memberName(annotation, method.name, "invoke", "call", "new", "create");
                    require(target, invoked, where, problems);
                } else {
                    // Every injector annotation names its targets in "method"
                    for (Object selector : values(annotation, "method")) {
                        require(target, selector.toString(), where, problems);
                        requireInvocations(target, selector.toString(), annotation, where, problems);
                    }
                }
            }
        }
    }

    private static void require(ClassNode target, String selector, String where, List<String> problems) {
        if (selector.startsWith("/")) return; // regular expression selectors are not checked
        if (matching(target, selector).isEmpty()) problems.add(where + "method " + selector + " is not in " + target.name);
    }

    // An @At(INVOKE) target must be called in the method, or the injector silently does nothing
    private static void requireInvocations(ClassNode target, String selector, AnnotationNode injector, String where,
                                           List<String> problems) {
        if (selector.startsWith("/")) return;
        for (Object value : values(injector, "at")) {
            AnnotationNode at = (AnnotationNode) value;
            if (!values(at, "value").contains("INVOKE")) continue;
            for (Object invoked : values(at, "target")) {
                boolean called = matching(target, selector).stream()
                        .flatMap(method -> Arrays.stream(method.instructions.toArray()))
                        .anyMatch(i -> i instanceof MethodInsnNode call
                                && ("L" + call.owner + ";" + call.name + call.desc).equals(invoked));
                if (!called) problems.add(where + selector + " no longer calls " + invoked);
            }
        }
    }

    private static List<MethodNode> matching(ClassNode target, String selector) {
        String member = selector.replace(" ", "");
        int descriptorStart = member.indexOf('(');
        String descriptor = descriptorStart < 0 ? null : member.substring(descriptorStart);
        String name = descriptorStart < 0 ? member : member.substring(0, descriptorStart);
        if (name.startsWith("L") && name.contains(";")) name = name.substring(name.indexOf(';') + 1);
        boolean wildcard = name.endsWith("*");
        String wanted = wildcard ? name.substring(0, name.length() - 1) : name;

        return target.methods.stream().filter(m ->
                (wildcard ? m.name.startsWith(wanted) : m.name.equals(wanted))
                        && (descriptor == null || m.desc.equals(descriptor))).toList();
    }

    private static String memberName(AnnotationNode annotation, String methodName, String... prefixes) {
        for (Object value : values(annotation, "value")) {
            if (!value.toString().isEmpty()) return value.toString();
        }
        String name = methodName.substring(methodName.lastIndexOf('$') + 1);
        for (String prefix : prefixes) {
            if (name.startsWith(prefix) && name.length() > prefix.length()) {
                String rest = name.substring(prefix.length());
                return Character.toLowerCase(rest.charAt(0)) + rest.substring(1);
            }
        }
        return name;
    }

    private static ClassNode mixinClass(String name) throws IOException {
        String relative = (config.get("package").getAsString() + "." + name).replace('.', '/') + ".class";
        for (Path directory : classDirectories) {
            Path file = directory.resolve(relative);
            if (Files.exists(file)) return read(Files.readAllBytes(file));
        }
        return fail("No class file for mixin " + name);
    }

    private static ClassNode targetClass(String internalName) throws IOException {
        // "targets" may write nested classes with dots
        String candidate = internalName;
        while (true) {
            URL url = targets.findResource(candidate + ".class");
            if (url != null) {
                try (InputStream stream = url.openStream()) {
                    return read(stream.readAllBytes());
                }
            }
            int slash = candidate.lastIndexOf('/');
            if (slash < 0) return null;
            candidate = candidate.substring(0, slash) + "$" + candidate.substring(slash + 1);
        }
    }

    private static ClassNode read(byte[] bytes) {
        ClassNode node = new ClassNode();
        new ClassReader(bytes).accept(node, ClassReader.SKIP_FRAMES);
        return node;
    }

    private static List<AnnotationNode> concat(List<AnnotationNode> first, List<AnnotationNode> second) {
        List<AnnotationNode> all = new ArrayList<>();
        if (first != null) all.addAll(first);
        if (second != null) all.addAll(second);
        return all;
    }

    private static boolean hasAnnotation(String descriptor, List<AnnotationNode> first, List<AnnotationNode> second) {
        return annotation(concat(first, second), descriptor) != null;
    }

    private static AnnotationNode annotation(List<AnnotationNode> annotations, String descriptor) {
        if (annotations == null) return null;
        return annotations.stream().filter(a -> a.desc.equals(descriptor)).findFirst().orElse(null);
    }

    private static List<?> values(AnnotationNode annotation, String attribute) {
        if (annotation.values == null) return List.of();
        for (int i = 0; i < annotation.values.size(); i += 2) {
            if (annotation.values.get(i).equals(attribute)) {
                Object value = annotation.values.get(i + 1);
                return value instanceof List<?> list ? list : List.of(value);
            }
        }
        return List.of();
    }
}
