plugins {
    id("com.android.application")
    id("kotlin-android")
    // Flutter plugin kaldırıldı
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.lanedetectionopencv"
    compileSdk = 35 // Flutter değişkeni yerine direkt değer
    ndkVersion = "27.0.12077973"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    defaultConfig {
        applicationId = "com.example.lanedetectionopencv"
        // Flutter referansları kaldırıldı
        minSdk = 21 // Direkt değer
        targetSdk = 35 // Direkt değer
        versionCode = 1 // Direkt değer
        versionName = "1.1.0"

        externalNativeBuild {
            cmake {
                cppFlags += listOf("-frtti", "-fexceptions", "-std=c++17")
                arguments += listOf("-DANDROID_STL=c++_shared")
            }
        }
    }

    buildTypes {
        release {
            // İmzalama yapılandırması
            signingConfig = signingConfigs.getByName("debug")
        }

    }

    externalNativeBuild {
        cmake {
            path = file("../CMakeLists.txt")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.guava:guava:33.4.0-android")
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.camera:camera-extensions:1.4.2")
    implementation("com.google.mlkit:face-mesh-detection:16.0.0-beta1")
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val cameraxVersion = "1.4.2"
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
}

