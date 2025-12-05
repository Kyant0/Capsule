plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(21)
    androidTarget {
        withSourcesJar(true)
        publishLibraryVariants("release")
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    wasmJs {
        browser()
    }
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.foundation)
            implementation(compose.ui)
        }
    }
}

android {
    namespace = "com.kyant.capsule"
    compileSdk {
        version = release(36)
    }
    buildToolsVersion = "36.1.0"

    defaultConfig {
        minSdk = 21

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.foundation)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("io.github.kyant0", "capsule", "2.1.2")

    pom {
        name.set("Capsule")
        description.set("Jetpack Compose smooth corners")
        inceptionYear.set("2025")
        url.set("https://github.com/Kyant0/Capsule")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("Kyant0")
                name.set("Kyant")
                url.set("https://github.com/Kyant0")
            }
        }
        scm {
            url.set("https://github.com/Kyant0/Capsule")
            connection.set("scm:git:git://github.com/Kyant0/Capsule.git")
            developerConnection.set("scm:git:ssh://git@github.com/Kyant0/Capsule.git")
        }
    }
}
