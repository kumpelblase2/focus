plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.oshi:oshi-core:4.6.1")
    compile(group = "org.jdom", name = "jdom2", version = "2.0.6")
    // technically I don't want this, but OSHI uses it and otherwise prints an error :/
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-nop:1.7.30")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
