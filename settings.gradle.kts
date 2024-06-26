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
        mavenLocal()
        mavenCentral()
        jcenter()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Cineflix"
include(":app")
