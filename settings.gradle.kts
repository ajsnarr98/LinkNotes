rootProject.name = "LinkNotes-Both"
include("app", "desktop", "linknotes_network")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LinkNotes"
include(":desktop")
include(":app")
include(":network")

