import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.IOException
import java.io.Serializable
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant

// A build of this mod and the Iris (Oculus on Forge) version it is built against
data class ShaderModPin(
    val build: String,
    val loader: String,
    val minecraftReleases: List<String>,
    val mod: String,
    val versionId: String,
) : Serializable

private data class ModrinthVersion(
    val id: String,
    val number: String,
    val type: String,
    val published: Instant,
    val loaders: List<String>,
    val minecraft: List<String>,
    val changelog: String,
)

private const val MAX_CHANGELOG_LENGTH = 3000

// Seconds to wait before each try to reach Modrinth
private val SECONDS_BEFORE_TRY = listOf(0L, 5L, 10L, 30L)

// Leaves out snapshots, pre-releases and so on
private val MINECRAFT_RELEASE = Regex("""\d+(\.\d+)+""")

private val minecraftOrder = Comparator<String> { first, second ->
    val a = first.split('.').map(String::toInt)
    val b = second.split('.').map(String::toInt)
    (0 until maxOf(a.size, b.size)).map { a.getOrElse(it) { 0 }.compareTo(b.getOrElse(it) { 0 }) }
        .firstOrNull { it != 0 } ?: 0
}

abstract class CheckIrisUpdates : DefaultTask() {
    @get:Input
    abstract val pins: ListProperty<ShaderModPin>

    @get:OutputFile
    abstract val report: RegularFileProperty

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun check() {
        val reportFile = report.get().asFile
        // An old report must not be mistaken for the result of this run
        reportFile.delete()

        val builds = pins.get().sortedBy { it.build }
        val versions = builds.map { it.mod }.distinct().associateWith(::fetchVersions)
        val sections = listOfNotNull(
            newerBuilds(builds, versions),
            unsupportedVersions(builds, versions),
        )

        val text = buildString {
            appendLine("# Iris and Oculus updates")
            appendLine()
            appendLine("Checked ${builds.size} builds against Modrinth on ${Instant.now().toString().take(10)}.")
            appendLine()
            if (sections.isEmpty()) appendLine("Everything is up to date.") else sections.forEach { appendLine(it) }
        }
        reportFile.parentFile.mkdirs()
        reportFile.writeText(text)

        if (sections.isEmpty()) {
            logger.lifecycle("\u001B[32m✓ Iris and Oculus are up to date for all ${builds.size} builds\u001B[0m")
            return
        }
        logger.lifecycle(text)
        throw GradleException("There are Iris or Oculus updates, see ${reportFile.absolutePath}")
    }

    // Builds published after the one in use, for the same loader and Minecraft versions
    private fun newerBuilds(builds: List<ShaderModPin>, versions: Map<String, List<ModrinthVersion>>): String? {
        val missing = mutableListOf<ShaderModPin>()
        val found = mutableMapOf<Pair<String, ModrinthVersion>, MutableList<String>>()
        for (build in builds) {
            val all = versions.getValue(build.mod)
            val pinned = all.find { it.id == build.versionId }
            if (pinned == null) {
                missing.add(build)
                continue
            }
            all.filter {
                it.published > pinned.published && build.loader in it.loaders
                        && it.minecraft.any(build.minecraftReleases::contains)
            }.forEach {
                found.getOrPut(build.mod to it) { mutableListOf() }.add("${build.build} (uses ${pinned.number})")
            }
        }
        if (found.isEmpty() && missing.isEmpty()) return null

        return buildString {
            appendLine("## Newer builds")
            missing.forEach {
                appendLine()
                appendLine("**${it.build}:** the ${it.mod} version `${it.versionId}` in use is no longer on Modrinth.")
            }
            found.entries.sortedByDescending { it.key.second.published }.forEach { (key, usedBy) ->
                val (mod, version) = key
                appendLine()
                appendLine("### ${title(mod)} ${version.number} (${version.type}, ${version.published.toString().take(10)})")
                appendLine()
                appendLine("- Newer for: ${usedBy.joinToString()}")
                appendLine("- Minecraft: ${version.minecraft.joinToString()}")
                appendLine("- Version ID: `${version.id}`")
                appendLine("- ${link(mod, version)}")
                if (version.changelog.isNotBlank()) {
                    appendLine()
                    val changelog = version.changelog.trim()
                    val shortened = if (changelog.length <= MAX_CHANGELOG_LENGTH) changelog
                    else changelog.take(MAX_CHANGELOG_LENGTH) + " […]"
                    shortened.lines().forEach { appendLine("> $it") }
                }
            }
            appendLine()
            appendLine("## What to do")
            appendLine()
            appendLine("1. Put the new version ID in `versions/<build>/stonecutter.properties.toml` and run `./gradlew \":<build>:build\"`")
            appendLine("2. Check the Iris commits for backported fixes and features, a changelog may not name them, and update `includedSince` in `Fixes.java` and `Features.java`")
        }
    }

    // A loader which lacks a Minecraft version that is supported on another one, or a newer Minecraft version
    private fun unsupportedVersions(builds: List<ShaderModPin>, versions: Map<String, List<ModrinthVersion>>): String? {
        val supported = builds.groupBy { it.loader }
            .mapValues { (_, list) -> list.flatMap { it.minecraftReleases }.toSet() }
        val anywhere = supported.values.flatten().toSet()
        val newest = anywhere.maxWithOrNull(minecraftOrder) ?: return null

        val lines = builds.map { it.mod to it.loader }.distinct().flatMap { (mod, loader) ->
            versions.getValue(mod)
                .filter { loader in it.loaders }
                .flatMap { version -> version.minecraft.map { it to version } }
                .filter { (minecraft, _) ->
                    MINECRAFT_RELEASE.matches(minecraft) && minecraft !in supported.getValue(loader)
                            && (minecraft in anywhere || minecraftOrder.compare(minecraft, newest) > 0)
                }
                .groupBy({ it.first }, { it.second })
                .toSortedMap(minecraftOrder)
                .map { (minecraft, found) ->
                    val latest = found.maxBy { it.published }
                    "- **$loader $minecraft:** ${title(mod)} ${latest.number}, ${link(mod, latest)}"
                }
        }
        if (lines.isEmpty()) return null
        return "## Not supported yet\n\n" + lines.joinToString("\n") + "\n"
    }

    private fun title(mod: String) = mod.replaceFirstChar(Char::uppercase)

    private fun link(mod: String, version: ModrinthVersion) = "https://modrinth.com/mod/$mod/version/${version.id}"

    private fun fetchVersions(mod: String): List<ModrinthVersion> {
        val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build()
        val request = HttpRequest.newBuilder(URI("https://api.modrinth.com/v2/project/$mod/version"))
            .header("User-Agent", "gradle-update-check")
            .timeout(Duration.ofSeconds(60))
            .build()

        var problem = ""
        for (seconds in SECONDS_BEFORE_TRY) {
            Thread.sleep(seconds * 1000)
            try {
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())
                if (response.statusCode() == 200) return parse(response.body())
                problem = "HTTP ${response.statusCode()}"
            } catch (e: IOException) {
                problem = e.toString()
            }
        }
        throw GradleException("Could not get the $mod versions from Modrinth: $problem")
    }

    @Suppress("UNCHECKED_CAST")
    private fun parse(json: String) = (JsonSlurper().parseText(json) as List<Map<String, Any?>>).map {
        ModrinthVersion(
            id = it["id"] as String,
            number = it["version_number"] as String,
            type = it["version_type"] as String,
            published = Instant.parse(it["date_published"] as String),
            loaders = it["loaders"] as List<String>,
            minecraft = it["game_versions"] as List<String>,
            changelog = it["changelog"] as String? ?: "",
        )
    }
}
