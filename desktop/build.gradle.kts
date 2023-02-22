plugins {
//    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.2.2"
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

//tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class).all {
//    kotlinOptions {
//        jvmTarget = "11"
//    }
//}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

//compose.desktop {
//    application {
//        mainClass = "MainKt"
//    }
//}
