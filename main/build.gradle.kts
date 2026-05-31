import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.File

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization")
}

group   = "io.legendaryos"
version = "0.0.1"

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.components.resources)
    implementation(compose.animation)

    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-client-websockets:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")

    implementation("com.jcraft:jsch:0.1.55")
    implementation("com.github.tulskiy:jkeymaster:1.3")
}

compose.desktop {
    application {
        mainClass = "legendaryos.MainKt"

        nativeDistributions {
            // BEZ .deb — tylko RPM (LegendaryOS/Fedora) i AppImage
            targetFormats(TargetFormat.Rpm, TargetFormat.AppImage)

            packageName    = "LegendaryOS-App"
            packageVersion = "0.0.1"
            description    = "LegendaryOS App — centrum zarządzania LegendaryOS, HackerOS i Android"
            vendor         = "LegendaryOS Project"
            copyright      = "© 2024 LegendaryOS Project. GPLv3"

            linux {
                // iconFile — ustawiamy tylko jeśli plik istnieje
                // (unikamy błędu "input file does not exist" gdy brakuje ikony)
                val iconPath = project.file("src/main/resources/icon.png")
                if (iconPath.exists()) {
                    iconFile.set(iconPath)
                }
                menuGroup      = "LegendaryOS"
                appCategory    = "Utility"
                rpmLicenseType = "GPLv3"
            }
        }
    }
}
