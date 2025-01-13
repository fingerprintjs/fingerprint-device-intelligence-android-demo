import com.android.build.gradle.internal.api.BaseVariantOutputImpl

@Suppress("PropertyName")
val VERSION_NAME = "3.2.1"

@Suppress("PropertyName")
val VERSION_CODE = 27

@Suppress("PropertyName")
val SDK_VERSION_NAME = "2.7.0"

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlinx-serialization")
}

android {
    namespace = "com.fingerprintjs.android.fpjs_pro_demo"
    compileSdk = 34


    externalNativeBuild {
        cmake {
            path = File("src/main/cpp/CMakeLists.txt")
        }
    }


    defaultConfig {
        applicationId = "com.fingerprintjs.android.fpjs_pro_demo"
        minSdk = 21
        targetSdk = 34
        versionCode = VERSION_CODE
        versionName = VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "SDK_VERSION_NAME", "\"${SDK_VERSION_NAME}\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("RELEASE_SIGN_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_SIGN_KEY_PASSWORD")
        }
    }

    buildTypes {
        // Default build type, debug symbols are enabled, minification is disabled,
        // includes an option of mocking responses of Fingerprint's servers.
        debug {
            buildConfigField("boolean", "ALLOW_MOCKS", "true")
        }
        // Production release build variant
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ALLOW_MOCKS", "false")
        }
        // Use this build variant for testing the app locally with minification enabled and release
        // level of performance, but also with the mocking functionality that the debug build type has.
        create("debugOptimized") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            buildConfigField("boolean", "ALLOW_MOCKS", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        val variant = this
        this.outputs.all {
            (this as? BaseVariantOutputImpl)?.outputFileName =
                "FPJS-Pro-Playground-${variant.name}-${variant.versionName}.apk"
        }
    }
}

dependencies {
    val useFpProDebugVersion =
        false // switch to true when needed to debug the locally built library

    //core
    implementation(libs.androidx.ktx.core)
    implementation(libs.androidx.naviation.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.core.splashscreen)

    //compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.preview)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)

    //shimmer
    implementation(libs.valentinilk.compose.shimmer)

    //network
    implementation(libs.squareup.okHttp3)

    //kotlin
    implementation(libs.michaelbull.kotlin.result)
    implementation(libs.michaelbull.kotlin.result.coroutine)
    implementation(libs.jetbrains.kotlinx.serialization.json)

    //di
    implementation(libs.google.dagger)
    kapt(libs.google.dagger.compiler)

    //security
    implementation(libs.androidx.security.crypto)
    if (useFpProDebugVersion) implementation(libs.fingerprint.pro.debug) else implementation(libs.fingerprint.pro)

    //testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
