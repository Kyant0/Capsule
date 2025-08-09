plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    androidTarget()

    js(IR) {
        browser {
            outputModuleName = "demo"
            commonWebpackConfig {
                outputFileName = "demo.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":capsule"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "com.kyant.capsule.demo"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.kyant.capsule.demo"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            vcsInfo.include = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            pickFirsts += arrayOf(
                "META-INF/androidx.compose.ui_ui.version"
            )
            excludes += arrayOf(
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json",
                "kotlin/**",
                "META-INF/*.version",
                "META-INF/**/LICENSE.txt"
            )
        }
        dex {
            useLegacyPackaging = true
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
    lint {
        checkReleaseBuilds = false
    }
}
