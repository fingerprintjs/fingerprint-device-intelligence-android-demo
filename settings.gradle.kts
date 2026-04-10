@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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

        maven {
            url = uri("https://maven.fpregistry.io/private-releases")
            credentials {
                username = providers.gradleProperty("privateMavenUser").orNull ?: System.getenv("PRIVATE_MAVEN_USER")
                password = providers.gradleProperty("privateMavenPassword").orNull ?: System.getenv("PRIVATE_MAVEN_PASSWORD")
            }
        }
    }
}

rootProject.name = "Fingerprint Pro"
include(":app")
