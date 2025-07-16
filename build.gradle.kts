import kotlin.io.path.createDirectory
import kotlin.io.path.exists

plugins {
    java
    application
    kotlin("jvm") version "2.1.21"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "dev.tonimatas"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.tonimatas.dev/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:6.0.0-preview") {
        exclude(module = "opus-java")
    }

    // https://github.com/qos-ch/logback/releases
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("dev.tonimatas:CJDA:1.0.3")
}

application {
    mainClass = "dev.tonimatas.botstudio.Main"
}

tasks.named<JavaExec>("run") {
    val path = rootDir.toPath().resolve("run")
    workingDir = path.toFile()
    if (!path.exists()) path.createDirectory()
}

tasks.compileJava {
    options.encoding = "UTF-8"
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    dependsOn("shadowJar")
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    archiveClassifier.set("")

    minimize {
        exclude(dependency("ch.qos.logback:logback-classic:.*"))
    }

    manifest {
        attributes("Main-Class" to "dev.tonimatas.botstudio.Main")
    }
}
