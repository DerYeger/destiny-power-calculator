plugins {
    java
    application
    kotlin("jvm") version "1.3.50"
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("edu.sc.seis.launch4j") version "2.4.6"
}

group = "eu.yeger"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "eu.yeger.dpc.AppLauncher"
}

val javaVersion = JavaVersion.VERSION_12
val junit5Version = "5.5.2"

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

javafx {
    version = javaVersion.toString()
    modules("javafx.controls")
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

    shadowJar {
        archiveFileName.set("dpc.jar")
        destinationDirectory.set(File("build/tmp/deploy"))
    }

    create<Zip>("publish") {
        group = "distribution"
        dependsOn(shadowJar, createExe)
        archiveFileName.set("dpc.zip")
        from("src/java-runtime", "build/tmp/deploy/DestinyPowerCalculator.exe")
    }

    launch4j {
        bundledJreAsFallback = false
        bundledJrePath = "runtime"
        jar = "dpc.jar"
        jreMinVersion = javaVersion.toString()
        mainClassName = application.mainClassName
        outfile = "DestinyPowerCalculator.exe"
        outputDir = "../build/tmp/deploy"
    }
}
