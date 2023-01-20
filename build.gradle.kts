plugins {
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
}

subprojects {
    apply(plugin = "org.springframework.cloud.java-conventions")
    apply(plugin = "com.ll.publish")
}
