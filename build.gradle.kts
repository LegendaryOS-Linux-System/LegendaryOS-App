plugins {
    kotlin("jvm") version "1.9.23" apply false
    kotlin("android") version "1.9.23" apply false
    id("org.jetbrains.compose") version "1.6.2" apply false
    id("com.android.application") version "8.3.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
