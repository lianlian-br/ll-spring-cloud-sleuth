dependencies {
    implementation(project(":ll-sleuth-instrumentation"))

    implementation("io.micrometer:micrometer-tracing")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-context")
    implementation("org.springframework:spring-context")
}
