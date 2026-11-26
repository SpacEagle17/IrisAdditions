import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestListener
import org.gradle.api.tasks.testing.TestResult
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.Properties

// One Minecraft version and loader, configured by configureModNode with everything the loaders share
class ModNode(
    val loader: String,
    val minecraft: String,
    val property: (String) -> String,
    val minecraftReleases: List<String>,
) {
    val modId get() = property("mod.id")
    val modName get() = property("mod.name")
    val javaVersion get() = property("deps.java").toInt()
}

private val metadataFiles = mapOf(
    "fabric" to listOf("fabric.mod.json"),
    "forge" to listOf("META-INF/mods.toml", "pack.mcmeta"),
    "neoforge" to listOf("META-INF/neoforge.mods.toml"),
)

fun Project.baseModVersion(): String {
    val file = rootProject.file("version.properties")
    val text = providers.fileContents(rootProject.layout.projectDirectory.file(file.name)).asText.get()
    return Properties().apply { load(text.reader()) }.getProperty("mod_version")
        ?: error("version.properties has no mod_version")
}

fun Project.configureModNode(node: ModNode, modJar: Provider<RegularFile>, mixinRefmap: String? = null) {
    val modVersion = baseModVersion()
    version = "$modVersion+mc${node.minecraft}-${node.loader}"
    extensions.configure<BasePluginExtension> { archivesName.set(node.modName) }

    configureJava(node)
    configureResources(node, mixinRefmap)
    configureTests(node)

    tasks.withType<Jar>().configureEach {
        val licenseName = "LICENSE_${node.modName}.txt"
        from(rootProject.file("LICENSE")) { rename { licenseName } }
        from(rootProject.file("3rd_party_licenses")) { into("3rd_party_licenses") }
    }

    tasks.register<Copy>("collectJar") {
        group = "build"
        description = "Copies the mod jar into the root build/libs folder"
        from(modJar)
        into(rootProject.layout.buildDirectory.dir("libs"))
        mustRunAfter(":cleanLibs")
    }

    configurePublishing(node, modVersion, modJar)
}

private fun Project.configureJava(node: ModNode) {
    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(node.javaVersion))
    }
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(node.javaVersion)
        options.compilerArgs.addAll(listOf("-Xlint:-options", "-Xlint:-processing", "-XDstringConcat=inline"))
    }
}

private fun Project.configureResources(node: ModNode, mixinRefmap: String?) {
    val fullVersion = version.toString()
    tasks.named<ProcessResources>("processResources") {
        val tokens = mutableMapOf(
            "name" to node.modName,
            "version" to fullVersion,
            "authors" to node.property("mod.authors"),
            "license" to node.property("mod.license"),
            "description" to node.property("mod.description"),
            "homepage" to node.property("mod.homepage"),
            "sources" to node.property("mod.sources"),
            "issues" to node.property("mod.issues"),
            "minecraft" to node.property("mod.mc_compat"),
            "java" to node.javaVersion.toString(),
        )
        if (node.loader == "fabric") tokens["loaderMin"] = node.property("mod.loader_min")
        inputs.properties(tokens)
        inputs.property("mixinRefmap", mixinRefmap.orEmpty())

        filesMatching(metadataFiles.getValue(node.loader)) { expand(tokens) }
        // The mixin config holds real values so the IDE can read it, what differs per version is changed here
        val refmapLine = mixinRefmap?.let { "\n  \"refmap\": \"$it\"," }.orEmpty()
        val javaLevel = "\"JAVA_${node.javaVersion}\""
        filesMatching("*.mixins.json") {
            filter { line: String ->
                if (line == "{") line + refmapLine else line.replace(Regex("\"JAVA_\\d+\""), javaLevel)
            }
        }
        exclude(metadataFiles.filterKeys { it != node.loader }.values.flatten())
    }
}

private fun Project.configureTests(node: ModNode) {
    dependencies {
        "testImplementation"(platform("org.junit:junit-bom:5.13.4"))
        "testImplementation"("org.junit.jupiter:junit-jupiter")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
        // For MixinTargetTest. Only preferred, some Minecraft setups pin their own
        "testImplementation"("org.ow2.asm:asm-tree") { version { prefer("9.9.1") } }
        "testImplementation"("com.google.code.gson:gson") { version { prefer("2.11.0") } }
    }

    val main = extensions.getByType<SourceSetContainer>().named("main")
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        val targets = files(main.map { it.compileClasspath })
        val classes = files(main.map { it.output.classesDirs })
        val resources = main.map { it.output.resourcesDir!! }
        inputs.files(targets).withPropertyName("mixinTargetClasspath")
        jvmArgumentProviders.add {
            listOf(
                "-DirisAdditions.test.targetClasspath=${targets.asPath}",
                "-DirisAdditions.test.classes=${classes.asPath}",
                "-DirisAdditions.test.resources=${resources.get().absolutePath}",
            )
        }
        testLogging {
            events("failed", "skipped")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = false
        }
        outputs.upToDateWhen { false }
        // Log4j writes its log files into the working directory, this keeps them inside build/
        workingDir = temporaryDir

        // One line per test task, otherwise a passing run prints nothing at all
        val taskPath = path
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent != null) return
                val line = "$taskPath: ${result.testCount} tests, ${result.successfulTestCount} passed, " +
                        "${result.failedTestCount} failed, ${result.skippedTestCount} skipped"
                val passed = result.failedTestCount == 0L
                println(if (passed) "\u001B[32m✓ $line\u001B[0m" else "\u001B[31m✗ $line\u001B[0m")
            }
        })
    }

    // core/ stays plain Java
    val coreSources = rootProject.file("src/main/java/com/spaceagle17/irisadditions/core")
    val rootDir = rootProject.projectDir
    val checkCore = tasks.register("checkCoreSources") {
        group = "verification"
        description = "Fails if core/ imports Minecraft, Iris or loader classes."
        inputs.dir(coreSources)
        val stamp = layout.buildDirectory.file("tmp/checkCoreSources.stamp")
        outputs.file(stamp)
        doLast {
            val forbidden = Regex(
                """\b(net\.minecraft|com\.mojang|net\.irisshaders|net\.fabricmc|net\.minecraftforge|net\.neoforged|org\.spongepowered|org\.lwjgl)\.\w+"""
            )
            val problems = coreSources.walkTopDown().filter { it.isFile && it.extension == "java" }.flatMap { file ->
                file.readLines().mapIndexedNotNull { index, line ->
                    forbidden.find(line)?.let { "${file.relativeTo(rootDir).invariantSeparatorsPath}:${index + 1}: ${it.value}" }
                }
            }.toList()
            if (problems.isNotEmpty()) {
                error("core/ must not reference Minecraft, Iris or loader classes:\n  " + problems.joinToString("\n  "))
            }
            stamp.get().asFile.apply { parentFile.mkdirs() }.writeText("ok")
        }
    }
    tasks.named("check") { dependsOn(checkCore) }
}
