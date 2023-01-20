import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.repositories
import java.util.Properties

plugins {
    `maven-publish`
}

val versionProperties = Properties().apply {
    load(rootProject.file("version.properties").inputStream())
}

val nexusHost = "https://nexus.tools.llpaybr.com/"
val nexusUser: String? by project
val nexusPassword: String? by project
val usePrereleases: String? by project
val usePrereleasesBool = usePrereleases?.toBoolean()

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("$nexusHost/repository/maven-releases/")
            val prereleasesRepoUrl = uri("$nexusHost/repository/maven-prereleases/")
            url = if ((versionProperties["version.prerelease"] as? String?).isNullOrBlank())
                releasesRepoUrl else prereleasesRepoUrl

            version = versionProperties["version.semver"] as String

            credentials {
                username = System.getenv("NEXUS_CREDENTIALS_USR") ?: nexusUser
                password = System.getenv("NEXUS_CREDENTIALS_PSW") ?: nexusPassword
            }
        }
    }
}