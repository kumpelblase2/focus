import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "de.eternalwings"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/hotkeytlt/maven")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile(project(":library"))
    implementation("com.github.h0tk3y.betterParse:better-parse-jvm:0.4.0-alpha-3")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    implementation("com.google.code.gson:gson:2.8.6")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
