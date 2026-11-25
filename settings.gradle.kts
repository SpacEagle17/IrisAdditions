pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10"
    id("dev.kikugie.loom-back-compat") version "0.4.3"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

stonecutter {
    create(rootProject) {
        fun minecraft(version: String, vararg loaders: String) {
            for (loader in loaders) version("$version-$loader", version).buildscript("build.$loader.gradle.kts")
        }

        minecraft("1.20.1", "fabric", "forge")
        minecraft("1.21.1", "fabric", "neoforge")
        minecraft("1.21.11", "fabric", "neoforge")
        minecraft("26.1.2", "fabric", "neoforge")
        minecraft("26.2", "fabric", "neoforge")
        minecraft("26.3", "fabric", "neoforge")

        // Run "Reset active project" before committing
        vcsVersion = "26.3-fabric"
    }
}

rootProject.name = "IrisAdditions"
