plugins {
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
}

val node = ModNode(
    loader = "fabric",
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

val productionMods: Configuration = configurations.create("productionMods") { isTransitive = false }

dependencies {
    fun modrinth(slug: String, property: String) = "maven.modrinth:$slug:${sc.properties[property]}"

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings() // no-op on unobfuscated versions (26.1+)
    modImplementation("net.fabricmc:fabric-loader:${sc.properties["deps.fabric_loader"]}")

    modCompileOnly(modrinth("iris", "deps.iris"))

    // runClient: Iris, Sodium and the libraries Iris bundles
    modRuntimeOnly(modrinth("iris", "deps.iris"))
    modRuntimeOnly(modrinth("sodium", "deps.sodium"))
    // Below 26.1 Loom drops the list of Fabric API modules bundled in Sodium and Iris, so runClient needs them from here
    if (sc.current.parsed < "26.1") {
        modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${sc.properties["deps.fabric_api"]}")
    }
    runtimeOnly("io.github.douira:glsl-transformer:${sc.properties["deps.glsl_transformer"]}")
    runtimeOnly("org.anarres:jcpp:1.4.14")
    runtimeOnly("org.antlr:antlr4-runtime:4.13.1")

    productionMods(modrinth("iris", "deps.iris"))
    productionMods(modrinth("sodium", "deps.sodium"))
}

// Loom starts 1.20.1 on a newer LWJGL than players have, which Sodium 0.5 refuses
val clientJvmArgs = if (sc.current.parsed < "1.20.2") listOf("-Dsodium.checks.issue2561=false") else emptyList()

loom {
    runConfigs.all {
        generateRunConfig = true
        runDirectory = rootProject.file("run/${sc.current.project}")
        jvmArguments.addAll(clientJvmArgs)
    }
}

// Client with the finished jar and the released mods, as players run it
tasks.register<net.fabricmc.loom.task.prod.ClientProductionRunTask>("runProductionClient") {
    group = "fabric"
    mods.from(productionMods)
    javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
    runDir.set(rootProject.layout.projectDirectory.dir("run/${sc.current.project}-production"))
    jvmArgs.addAll(clientJvmArgs)
    outputs.upToDateWhen { false }
}

configureModNode(node, loomx.modJar.flatMap { it.archiveFile })
