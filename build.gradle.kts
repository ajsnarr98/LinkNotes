import groovy.lang.MissingPropertyException
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
//    id("com.android.application") version "7.4.1" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.buildconfig.gradle) apply false
}

group = "com.github.ajsnarr98"

allprojects {
    readGradleLocalProperties()

    val externalVarOrNull: (key: String) -> String? = { key ->
        (ext.get("gradleLocalProperties") as Properties).getProperty(key)
            ?: System.getenv(key)
    }
    val externalVar: (key: String) -> String = { key ->
        externalVarOrNull(key) ?: throw MissingPropertyException(
            "Couldn't find required variable $key. Did you remember to set it in local.properties or your environment?"
        )
    }
    ext.set("externalVarOrNull", externalVarOrNull)
    ext.set("externalVar", externalVar)
}

fun Project.readGradleLocalProperties() {
    // read local properties
    val allProperties = Properties()
    properties.forEach { allProperties.setProperty(it.key, it.value.toString()) }
    try {
        allProperties.load(FileInputStream(rootProject.file("local.properties")))
    } catch (e: Exception) {
        println("Encountered exception while reading from local.properties: $e")
    }
    ext.set("gradleLocalProperties", allProperties)
}
