import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

group = "io.github.mayachen350"
version = Properties().run {
    load(FileInputStream("src/main/resources/bot.properties"))
    getProperty("version")
}
description = "Official bot of the Salon de Chesnay Discord Server."

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.24.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation("com.h2database:h2:2.2.224")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    manifest {
        attributes["Main-Class"] = "io.github.mayachen350.chesnaybot.AppKt"
    }
}

kotlin {
    jvmToolchain(17)
}