plugins {
    kotlin("jvm") version "2.4.0"
    application
}

group = "com.github.kanaria32"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Source: https://mvnrepository.com/artifact/com.opencsv/opencsv
    implementation("com.opencsv:opencsv:5.12.0")
}

application {
    mainClass.set("com.github.kanaria32.MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
