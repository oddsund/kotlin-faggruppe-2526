rootProject.name = "kotlin-faggruppe-2526"

// Workshop 1: Intro til Kotlin
include("1-intro")
include("1-intro:01-java-to-kotlin")
include("1-intro:02-data-objects")
include("1-intro:03-when-guards")
include("1-intro:04-nonlocal-control")
include("1-intro:05-context-params")

// Workshop 2: Testing
include("2-testing")
include("2-testing:felles")
include("2-testing:del1-assertions")
include("2-testing:del2-mocking")
include("2-testing:del3-mockless")
include("2-testing:del4-integration-architecture")

// Workshop 3: Spring Boot Part 1
include("3-kotlin-spring-boot-part1")

// Workshop 4: Spring Boot Part 2
include("4-kotlin-spring-boot-part2")

// Workshop 5: Ktor + Exposed
include("5-ktor-exposed")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
