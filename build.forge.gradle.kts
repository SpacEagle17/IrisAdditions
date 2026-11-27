// Forge, reobfuscated to SRG names
import dev.kikugie.stonecutter.controller.task.StonecutterTaskMutexService

plugins {
    id("net.neoforged.moddev.legacyforge")
    id("me.modmuss50.mod-publish-plugin")
}

val node = ModNode(
    loader = "forge",
    minecraft = sc.current.version,
    property = { sc.properties[it] },
    minecraftReleases = sc.properties.raw("mod.mc_releases").asList().map { it.toString() },
)
val mixinConfig = "${node.modId}.mixins.json"
val refmap = "${node.modId}.refmap.json"

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

legacyForge {
    version = "${sc.current.version}-${sc.properties["deps.forge"]}"

    mods {
        register(node.modId) { sourceSet(sourceSets.main.get()) }
    }

    // Tests need Minecraft's libraries, such as Log4j
    addModdingDependenciesTo(sourceSets.test.get())

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("run/${sc.current.project}")
        }
    }
}

// Must come after legacyForge {}, which creates the mod* configurations
dependencies {
    fun modrinth(slug: String, property: String) = "maven.modrinth:$slug:${sc.properties[property]}"

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    // Forge does not ship MixinExtras
    val mixinExtras = "${sc.properties["deps.mixin_extras"]}"
    compileOnly("io.github.llamalad7:mixinextras-common:$mixinExtras")
    annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinExtras")
    jarJar(implementation("io.github.llamalad7:mixinextras-forge:$mixinExtras")!!)

    modCompileOnly(modrinth("oculus", "deps.oculus"))
    compileOnly(modrinth("distanthorizonsapi", "deps.dh_api"))

    modRuntimeOnly(modrinth("oculus", "deps.oculus"))
    modRuntimeOnly(modrinth("embeddium", "deps.embeddium"))
}

mixin {
    add(sourceSets.main.get(), refmap)
    config(mixinConfig)
}

// Hides the Mixin processor's progress notes, which Gradle reports as warnings. Its real warnings stay
tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.add("-Aquiet=true")
}

tasks.jar {
    manifest.attributes("MixinConfigs" to mixinConfig)
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
    usesService(StonecutterTaskMutexService.create(gradle, "createMinecraftArtifacts"))
}

configureModNode(node, tasks.named<org.gradle.jvm.tasks.Jar>("reobfJar").flatMap { it.archiveFile }, mixinRefmap = refmap)
