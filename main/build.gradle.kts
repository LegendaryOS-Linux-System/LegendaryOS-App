import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "io.legendaryos"
version = "1.0.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.components.resources)

    // Networking / ADB bridge
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-cio:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // System tray & native
    implementation("com.github.tulskiy:jkeymaster:1.3")
}

compose.desktop {
    application {
        mainClass = "legendaryos.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
            packageName = "LegendaryOS-App"
            packageVersion = "1.0.0"
            description = "LegendaryOS App — bridge between LegendaryOS, HackerOS & Android"
            vendor = "LegendaryOS Project"
            linux {
                iconFile.set(project.file("src/main/resources/icon.png"))
                menuGroup = "LegendaryOS"
                appCategory = "Utility"
            }
        }
    }
}
