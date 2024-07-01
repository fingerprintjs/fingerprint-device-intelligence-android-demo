@file:Suppress("UnstableApiUsage")

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
        mavenLocal {
            mavenContent {
                includeVersionByRegex("com.fingerprint.android", "pro", ".*-debug")
            }
        }

        maven { url = uri("https://maven.fpregistry.io/releases") }
    }
}

rootProject.name = "Fingerprint Pro"
include(":app")
 