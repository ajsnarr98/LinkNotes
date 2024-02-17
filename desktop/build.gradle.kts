import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

fun mainResourceFile(resourcePath: String): File {
    return project.file("src/main/resources/$resourcePath")
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose.desktop)
    alias(libs.plugins.buildconfig.gradle)
}

val desktopBasePackageName = "com.github.ajsnarr98.linknotes.desktop"

group = desktopBasePackageName
version = "1.0.0"

buildConfig {
    buildConfigField("DEBUG", (externalVarOrNull("isRelease") ?: false) == false)
}

dependencies {
    implementation(project(":linknotes_network"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlin.reflect)
    implementation(libs.coroutines.swing)

    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.moshi.kotlin)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "${desktopBasePackageName}.MainKt"

        // make sure at least Java 17 is set for JAVA_HOME
        nativeDistributions {
            licenseFile.set(rootProject.file("LICENSE"))

            includeAllModules = true // TODO - determine if we need this or want to reduce size by only specifying specific modules
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)

            nativeDistributions {
                macOS {
                    iconFile.set(mainResourceFile("icon/ic_launcher.icns"))
                }
                windows {
                    iconFile.set(mainResourceFile("icon/ic_launcher.ico"))
                }
                linux {
                    iconFile.set(mainResourceFile("icon/ic_launcher.png"))
                }
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

@Suppress("UNCHECKED_CAST")
fun externalVarOrNull(key: String): String? {
    return (project.ext.get("externalVarOrNull") as (String) -> String?)(key)
}

@Suppress("UNCHECKED_CAST")
fun externalVar(key: String): String {
    return (project.ext.get("externalVar") as (String) -> String)(key)
}
