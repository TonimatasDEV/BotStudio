plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

apply(plugin = "com.github.johnrengelman.shadow")

group = "dev.tonimatas"
version = "1.0.0"

base {
    archivesName.set("BotStudio")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.19")
}

tasks.withType<Jar> {
    manifest.attributes(
        "Main-Class" to "dev.tonimatas.botstudio.BotStudio"
    )
}
