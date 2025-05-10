pluginManagement {
    // Flutter SDK yolunu kaldırdım

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.android.application") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.22" apply false
}

include(":app")
// Android modülü varsa, kullanımınıza göre tutabilirsiniz
include(":android")