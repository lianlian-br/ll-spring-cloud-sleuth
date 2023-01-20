import org.gradle.api.JavaVersion.VERSION_17

plugins {
    `java`
    `java-library`
}

val nexusHost = "https://nexus.tools.llpaybr.com/"
val nexusUser: String? by project
val nexusPassword: String? by project
val usePrereleases: String? by project
val usePrereleasesBool = usePrereleases?.toBoolean()

repositories {
    maven {
        name = "LianLian Nexus Server Maven Releases"
        url = uri("$nexusHost/repository/maven-public/")
        credentials {
            username = System.getenv("NEXUS_CREDENTIALS_USR") ?: nexusUser
            password = System.getenv("NEXUS_CREDENTIALS_PSW") ?: nexusPassword
        }
    }

    if (usePrereleasesBool ?: true) {
        maven {
            name = "LianLian Nexus Server Maven Pre-Releases"
            url = uri("$nexusHost/repository/maven-prereleases/")
            credentials {
                username = System.getenv("NEXUS_CREDENTIALS_USR") ?: nexusUser
                password = System.getenv("NEXUS_CREDENTIALS_PSW") ?: nexusPassword
            }
        }
    }

    dependencies {
        "implementation"(platform("org.springframework.cloud:spring-cloud-dependencies:2022.0.0"))
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:3.0.1"))
    }
}

group = "com.ll.sleuth-reactor-port"

java {
    sourceCompatibility = VERSION_17
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginExtension> {
    sourceCompatibility = VERSION_17
    withSourcesJar()
}
