import java.io.FileInputStream
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.gradleup.shadow") version "9.0.0-beta4"

    id("org.flywaydb.flyway") version "9.22.3"
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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.migration)
    implementation(libs.exposed.dao)
    implementation("mysql:mysql-connector-java:8.0.33")

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
    jvmToolchain(22)
}

flyway {
    Path("${project.projectDir}/src/main/resources/db/migration/").run {
        if (notExists())
            createDirectories()
    }

    url = "jdbc:mysql://192.168.0.188:3306/SalonChesnay"
    baselineOnMigrate = true
}