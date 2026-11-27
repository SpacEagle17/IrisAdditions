plugins {
    id("net.neoforged.moddev") version "2.0.148" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.148" apply false
}

stonecutter active "26.3-fabric"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)
    properties {
        tags(version, loader)
        // Stonecutter 0.10 only loads the root file by itself
        load(rootProject.file("versions/${current.project}/stonecutter.properties.toml"))
    }
    constants { match(loader, "fabric", "forge", "neoforge") }
}

val libsDir = layout.buildDirectory.dir("libs")
val zipName = "IrisAdditionsMod_UNZIP_ME.zip"

val cleanLibs = tasks.register<Delete>("cleanLibs") {
    group = "build"
    description = "Cleans the root build/libs directory"
    delete(libsDir)
}

val zipJars = tasks.register<Zip>("zipJars") {
    group = "build"
    description = "Zips all jars of build/libs"
    dependsOn(cleanLibs, stonecutter.tasks.names("collectJar"))
    // Only zip once every version compiled and passed its tests
    mustRunAfter(stonecutter.tasks.names("build"))
    from(libsDir) { include("*.jar") }
    destinationDirectory.set(libsDir)
    archiveFileName.set(zipName)
}

tasks.register("buildAll") {
    group = "build"
    description = "Builds and tests all versions, then collects the jars in build/libs and zips them"
    dependsOn(stonecutter.tasks.names("build"), zipJars)
    val libs = libsDir
    doLast {
        val jars = libs.get().asFile.listFiles { file -> file.extension == "jar" }.orEmpty().map { it.name }.sorted()
        println("\n\u001B[32mBuilt ${jars.size} jars into build/libs/\u001B[0m")
        jars.forEach { println("  $it") }
    }
}

// Running just "./gradlew" builds everything
defaultTasks("buildAll")

// Not part of any build, it needs the network. Each version adds itself in configureModNode
tasks.register<CheckIrisUpdates>("checkIrisUpdates") {
    group = "verification"
    description = "Fails if Modrinth has newer Iris or Oculus builds than the ones used here"
    report.set(layout.buildDirectory.file("reports/irisUpdates.md"))
}

val publishSession = PublishSession.of(project)
val changelogFile = file("CHANGELOG.md")
val modVersion = baseModVersion()

val validateChangelog = tasks.register("validateChangelog") {
    group = "publishing"
    doLast {
        if (!changelogFile.exists()) {
            changelogFile.writeText("### IrisAdditions $modVersion\n\n## Changes:\n- ...\n")
            throw GradleException("CHANGELOG.md was missing and has been created. Please update it with release notes and try again.")
        }
        if (!changelogFile.readText().contains(modVersion)) {
            throw GradleException("CHANGELOG.md does not contain version $modVersion. Please update the changelog before publishing.")
        }
        println("\u001B[32m✓ Changelog validated for version: $modVersion\u001B[0m")
    }
}

val confirmPublish = tasks.register("confirmPublish") {
    group = "publishing"
    usesService(publishSession)
    // Everything is built before asking, so no build output interrupts the prompt
    dependsOn(validateChangelog, "buildAll")

    val testRun = !isLivePublishing
    val projectIds = listOf("curseforge_project_id", "modrinth_project_id")
        .associateWith { providers.gradleProperty(it).orNull.orEmpty() }

    doLast {
        val reader = System.`in`.bufferedReader()
        fun ask(question: String): String {
            print(question)
            System.out.flush()
            return reader.readLine()?.lowercase()?.trim()
                ?: throw GradleException("No console input available. Run this task from a terminal.")
        }

        fun confirmed(question: String) = ask(question) in listOf("yes", "y")

        println("\n${"=".repeat(60)}\nPUBLISH CONFIRMATION\n${"=".repeat(60)}")
        println("Base Version: $modVersion")
        println("Test Run: ${if (testRun) "true" else "\u001B[31mfalse\u001B[0m"}")
        println("=".repeat(60))

        if (!confirmed("Are you sure you want to proceed? (yes/no): ")) throw GradleException("Publishing cancelled.")

        if (!testRun) {
            println("\n${"!".repeat(60)}\nLIVE PUBLISH WARNING\n${"!".repeat(60)}")
            println("\u001B[31mNOT A TEST RUN!\u001B[0m")
            println("This will perform a REAL UPLOAD to CurseForge and/or Modrinth!")
            println("Files will be publicly available immediately!\n${"!".repeat(60)}")
            if (!confirmed("\nAre you ABSOLUTELY SURE you want to proceed? (yes/no): ")) {
                throw GradleException("Live publishing cancelled. No files were uploaded.")
            }
        }

        val session = publishSession.get()
        when (ask("\nPublish to (1=Both, 2=CurseForge only, 3=Modrinth only): ")) {
            "1" -> println("Selected: Both platforms")
            "2" -> { session.skipModrinth = true; println("Selected: CurseForge only") }
            "3" -> { session.skipCurseforge = true; println("Selected: Modrinth only") }
            else -> throw GradleException("Invalid selection. Publishing cancelled.")
        }

        if (!testRun) {
            val selected = projectIds.filterKeys {
                if (it.startsWith("curseforge")) !session.skipCurseforge else !session.skipModrinth
            }
            val missing = selected.filterValues { it.isBlank() || '#' in it }.keys
            if (missing.isNotEmpty()) {
                throw GradleException("Set ${missing.joinToString()} in gradle.properties first. No files were uploaded.")
            }
        }
        println("\nConfirmation complete. Proceeding with publishing...\n")
    }
}

tasks.register("publishAll") {
    group = "publishing"
    description = "Publishes all versions, oldest Minecraft version first"
    dependsOn(confirmPublish, stonecutter.tasks.names("publishMods"))
    doLast {
        println("\n${"=".repeat(60)}\n\u001B[32mAll versions published successfully!\u001B[0m\n${"=".repeat(60)}")
    }
}

// One upload at a time: oldest version first, Fabric last within a version, so the newest files are listed on top
stonecutter tasks {
    val ordering = versionComparator.thenComparingInt { if (it.metadata.project.endsWith("fabric")) 1 else 0 }
    order("publishModrinth", ordering)
    order("publishCurseforge", ordering)
}
