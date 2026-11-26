import dev.kikugie.stonecutter.controller.task.StonecutterTaskMutexService

plugins {
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

val node = ModNode(
    loader = "neoforge",
    minecraft = sc.current.version,
    property = { sc.properties[it] },
    minecraftReleases = sc.properties.raw("mod.mc_releases").asList().map { it.toString() },
)

repositories {
    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

// Dev client mods go into mods/, NeoForge 21.1 does not unpack Sodium's nested jars from the classpath
val devMods: Configuration = configurations.create("devMods") { isTransitive = false }
val gameDir = rootProject.file("run/${sc.current.project}")

dependencies {
    fun modrinth(slug: String, property: String) = "maven.modrinth:$slug:${sc.properties[property]}"

    compileOnly(modrinth("iris", "deps.iris"))
    compileOnly(modrinth("distanthorizonsapi", "deps.dh_api"))
    // Nested in the Iris jar, so it is not on the compile classpath by itself
    compileOnly("io.github.douira:glsl-transformer:${sc.properties["deps.glsl_transformer"]}")

    devMods(modrinth("iris", "deps.iris"))
    devMods(modrinth("sodium", "deps.sodium"))
}

val prepareDevClient = tasks.register<Sync>("prepareDevClient") {
    from(devMods)
    into(gameDir.resolve("mods"))
    // Keeps other mods in the folder
    preserve {
        include("**")
        exclude("iris-*.jar", "sodium-*.jar")
    }
    // The dev-only GPU validation layer crashes Sodium on 1.21.11
    val clientConfig = gameDir.resolve("config/neoforge-client.toml")
    doLast {
        if (!clientConfig.exists()) {
            clientConfig.parentFile.mkdirs()
            clientConfig.writeText("enableB3DValidationLayer = false" + System.lineSeparator())
        }
    }
}
tasks.matching { it.name == "runClient" }.configureEach { dependsOn(prepareDevClient) }

neoForge {
    version = sc.properties["deps.neoforge"]

    mods {
        register(node.modId) { sourceSet(sourceSets.main.get()) }
    }

    // Tests need Minecraft's libraries, such as Log4j
    addModdingDependenciesTo(sourceSets.test.get())

    runs {
        register("client") {
            client()
            gameDirectory = gameDir
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
    // Memory hungry, one version at a time
    usesService(StonecutterTaskMutexService.create(gradle, "createMinecraftArtifacts"))
}

configureModNode(node, tasks.jar.flatMap { it.archiveFile })
