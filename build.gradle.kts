import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "4.0.4"
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
    implementation("com.github.ajalt:clikt:2.6.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("net.dongliu:gson-java8-datatype:1.1.0")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("focus-full")
        mergeServiceFiles()
        manifest {
            attributes(mapOf(
                "Main-Class" to "de.eternalwings.focus.MainKt",
                "Implementation-Version" to project.version
            ))
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
