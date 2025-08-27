plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20-RC"
    application
}

application{
    mainClass.set("io.github.xkaih.MainKt")
}

tasks.register<Jar>("fatJar") {
    group = "build"
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "io.github.xkaih.MainKt"
    }

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}

group = "io.github.xkaih"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}