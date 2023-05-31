import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.gradle.ext.Gradle

plugins {
    `maven-publish`
    kotlin("jvm") version "1.8.20"
    kotlin("kapt") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.6"
}

group = "gg.growly.experimental"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.1")

    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.9.0")
    implementation("org.spongepowered:configurate-yaml:4.0.0")
    implementation("org.spongepowered:configurate-yaml:4.0.0")

    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.12.1")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.12.1")

    compileOnly("org.spigotmc:plugin-annotations:1.2.3-SNAPSHOT")
    kapt("org.spigotmc:plugin-annotations:1.2.3-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
}

kotlin {
    jvmToolchain(jdkVersion = 17)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveFileName.set(
        "${project.name}.jar"
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.javaParameters = true
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

publishing {
    publications {
        register(
            name = "mavenJava",
            type = MavenPublication::class,
            configurationAction = shadow::component
        )
    }
}

tasks["build"]
    .dependsOn(
        "shadowJar",
        "publishMavenJavaPublicationToMavenLocal"
    )

idea {
    project {
        settings {
            runConfigurations {
                create<Gradle>("Build Plugin") {
                    setProject(project)
                    scriptParameters = "clean build"
                }
            }
        }
    }
}
