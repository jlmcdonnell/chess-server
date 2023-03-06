import com.google.common.util.concurrent.Uninterruptibles
import io.ktor.plugin.features.DockerImageRegistry
import io.ktor.plugin.features.JreVersion

plugins {
    kotlin("jvm") version "1.8.10"
    id("io.ktor.plugin") version "2.2.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

group = "dev.mcd.chess"
version = "0.0.1"

application {
    mainClass.set("dev.mcd.chess.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    docker {
        jreVersion.set(JreVersion.JRE_17)
        localImageName.set("chess-server")
        imageTag.set("latest")
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    val ktorVersion = "2.2.3"

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.insert-koin:koin-ktor:3.3.1")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("com.github.bhlangonijr:chesslib:1.3.3")

    testImplementation(enforcedPlatform("org.junit:junit-bom:5.9.2")) // JUnit 5 BOM
    testImplementation("org.junit.jupiter:junit-jupiter")

}
