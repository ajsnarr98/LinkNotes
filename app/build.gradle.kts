import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.parcelize)
}

group = "com.github.ajsnarr98"
version = "1.0-SNAPSHOT"

val keystoreProperties = Properties().apply {
    load(FileInputStream(rootProject.file("keystore.properties")))
}

android {
    namespace = "com.github.ajsnarr98.linknotes"

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as? String ?: throw IllegalArgumentException("release keyAlias not found in keystore.properties")
            keyPassword = keystoreProperties["keyPassword"] as? String ?: throw IllegalArgumentException("release keyPassword not found in keystore.properties")
            storeFile = file(keystoreProperties["storeFile"] as? String ?: throw IllegalArgumentException("release storeFile not found in keystore.properties"))
            storePassword = keystoreProperties["storePassword"] as? String ?: throw IllegalArgumentException("release storePassword not found in keystore.properties")
        }
    }

    compileSdk = 33
    defaultConfig {
        applicationId = "com.github.ajsnarr98.linknotes"
        minSdk = 24
        targetSdk = 33
        multiDexEnabled = true
        versionCode = 1
        versionName =  "1.0"
        testInstrumentationRunner =  "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        signingConfig = signingConfigs.getByName("debug")
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
        register("dev") {
            // used for CI building
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            isDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
//    buildToolsVersion '30.0.1'
}

dependencies {
    implementation(project(":linknotes_network"))

    testImplementation(kotlin("test"))
}

