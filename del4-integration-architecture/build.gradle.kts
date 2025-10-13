dependencies {
    implementation(project(":felles"))

    // For integration tests
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.postgresql:postgresql:42.7.1")

    // Konsist
    testImplementation("com.lemonappdev:konsist:0.17.3")
}