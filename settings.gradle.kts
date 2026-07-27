@file:Suppress("UnstableApiUsage")

import org.gradle.api.credentials.HttpHeaderCredentials
import org.gradle.authentication.http.HttpHeaderAuthentication

val localProperties = java.util.Properties().apply {
    val f = file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

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
            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = providers.gradleProperty("privateMavenRepoToken")
                    .orElse(providers.environmentVariable("PRIVATE_MAVEN_REPO_TOKEN"))
                    .getOrElse(localProperties.getProperty("PRIVATE_MAVEN_REPO_TOKEN", ""))
                    .let { "Bearer $it" }
            }
            authentication {
                create<HttpHeaderAuthentication>("header")
            }
            content {
                includeGroup("com.fingerprint.android")
            }
        }
    }
}

rootProject.name = "Fingerprint Pro"
include(":app")
