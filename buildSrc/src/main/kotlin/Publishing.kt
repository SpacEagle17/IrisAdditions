import me.modmuss50.mpp.ModPublishExtension
import me.modmuss50.mpp.PublishModTask
import me.modmuss50.mpp.ReleaseType
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.*

abstract class PublishSession : BuildService<BuildServiceParameters.None> {
    var skipCurseforge = false
    var skipModrinth = false

    companion object {
        fun of(project: Project): Provider<PublishSession> =
            project.gradle.sharedServices.registerIfAbsent("publishSession", PublishSession::class.java) {}
    }
}

val Project.isLivePublishing: Boolean
    get() = providers.gradleProperty("do_live_publishing").orNull.toBoolean()

// Without a pause the platforms can list the files in the wrong order
private const val SECONDS_BETWEEN_UPLOADS = 6L

internal fun Project.configurePublishing(node: ModNode, modVersion: String, modJar: Provider<RegularFile>) {
    val isForge = node.loader == "forge"
    val modrinthShaderMod = if (isForge) "oculus" else "iris"
    val curseforgeShaderMod = if (isForge) "oculus" else "irisshaders"
    val live = isLivePublishing

    extensions.configure<ModPublishExtension> {
        file.set(modJar)
        version.set(project.version.toString())
        displayName.set("${node.modName} $modVersion (${node.loader} ${node.minecraft})")
        changelog.set(providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
            .orElse("No changelog."))
        type.set(ReleaseType.STABLE)
        modLoaders.add(node.loader)
        if (node.loader == "fabric") modLoaders.add("quilt")
        dryRun.set(!live)

        curseforge {
            projectId.set(providers.gradleProperty("curseforge_project_id"))
            accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
            minecraftVersions.addAll(node.minecraftReleases)
            client.set(true)
            server.set(false)
            requires(curseforgeShaderMod)
        }
        modrinth {
            projectId.set(providers.gradleProperty("modrinth_project_id"))
            accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
            minecraftVersions.addAll(node.minecraftReleases)
            requires(modrinthShaderMod)
        }
    }

    val session = PublishSession.of(this)
    tasks.withType<PublishModTask>().configureEach {
        usesService(session)
        dependsOn(":confirmPublish")
        val toCurseforge = name == "publishCurseforge"
        val toModrinth = name == "publishModrinth"
        onlyIf("platform was selected in confirmPublish") {
            !(toCurseforge && session.get().skipCurseforge) && !(toModrinth && session.get().skipModrinth)
        }
        if (live) doFirst { Thread.sleep(SECONDS_BETWEEN_UPLOADS * 1000) }
    }
}
