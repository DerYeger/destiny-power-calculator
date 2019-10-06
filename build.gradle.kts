import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    kotlin("jvm") version "1.3.50"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "eu.yeger"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "eu.yeger.dplc.AppLauncher"
}

val javaVersion = JavaVersion.VERSION_12
val junit5Version = "5.5.2"

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

javafx {
    version = javaVersion.toString()
    modules = listOf("javafx.controls")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    implementation("com.google.code.gson:gson:2.8.6")
    
    implementation("eu.yeger:kotlin.javafx:0.1.2")

    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javafx.version:mac")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    withType<ShadowJar> {
        archiveName = "dplc.jar"
    }
}
