
plugins {
    java
}

dependencies {
    implementation(libs.coroutines.core)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}