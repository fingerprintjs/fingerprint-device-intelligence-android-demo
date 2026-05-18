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
                includeVersionByRegex("com.fingerprint.android", "pro", ".*")
            }
        }

        maven { url = uri("https://maven.fpregistry.io/releases") }

        maven {
            url = uri("https://maven.fpregistry.io/private-releases")
            credentials(PasswordCredentials::class) {
                username = providers.gradleProperty("privateMavenUser")
                    .orElse(providers.environmentVariable("PRIVATE_MAVEN_USER"))
                    .getOrElse("")
                password = providers.gradleProperty("privateMavenPassword")
                    .orElse(providers.environmentVariable("PRIVATE_MAVEN_PASSWORD"))
                    .getOrElse("")
            }
            content {
                includeGroup("com.fingerprint.android")
            }
        }
    }
}

rootProject.name = "Fingerprint Pro"
include(":app")
