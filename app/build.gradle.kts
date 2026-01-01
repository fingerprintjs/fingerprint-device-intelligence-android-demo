import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import java.util.Properties

val local = Properties().apply {
    val file = File(rootDir, "local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

@Suppress("PropertyName")
val VERSION_NAME="3.7.0"
@Suppress("PropertyName")
val VERSION_CODE=42
val useFpProDebugVersion =
    false // switch to true when needed to debug the locally built library
val fingerprintProLib = if (useFpProDebugVersion) libs.fingerprint.pro.debug else libs.fingerprint.pro.asProvider()

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.compose)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
}

val googleServicesFilename = "google-services.json"
val googleServicesFile = file(googleServicesFilename)

android {
    namespace = "com.fingerprintjs.android.fpjs_pro_demo"
    compileSdk = 35

    ndkVersion = "28.2.13676358"
    
    externalNativeBuild {
        cmake {
            path = File("src/main/cpp/CMakeLists.txt")
        }
    }


    defaultConfig {
        applicationId = "com.fingerprintjs.android.fpjs_pro_demo"
        minSdk = 23
        targetSdk = 35
        versionCode = VERSION_CODE
        versionName = VERSION_NAME

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "SDK_VERSION_NAME", "\"${fingerprintProLib.get().versionConstraint.requiredVersion}\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: local.getProperty("KEYSTORE_PASSWORD")
            keyAlias  = System.getenv("RELEASE_SIGN_KEY_ALIAS") ?: local.getProperty("RELEASE_SIGN_KEY_ALIAS")
            keyPassword = System.getenv("RELEASE_SIGN_KEY_PASSWORD") ?: local.getProperty("RELEASE_SIGN_KEY_PASSWORD")
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ALLOW_MOCKS", "false")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        // Use this build variant for testing the app locally with minification enabled and release
        // level of performance, but also with the mocking functionality that the debug build type has.
        create("debugOptimized") {
            isMinifyEnabled = true
            proguardFiles (getDefaultProguardFile ("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
            buildConfigField("boolean", "ALLOW_MOCKS", "true")
            ndk {
                debugSymbolLevel = "FULL"
            }
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
            (this as? BaseVariantOutputImpl)?.outputFileName = "FPJS-Pro-Playground-${variant.name}-${variant.versionName}.apk"
        }
    }
}

detekt {
    config.setFrom(file("$rootDir/config/detekt/detekt.yml"))
    baseline = file("$rootDir/config/detekt/baseline.xml")
    buildUponDefaultConfig = true
}

dependencies {

    //core
    implementation(libs.androidx.ktx.core)
    implementation(libs.androidx.naviation.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.fragment.ktx)

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

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)

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
    implementation(fingerprintProLib)

    //testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    detektPlugins(libs.detekt.formatting)
}

afterEvaluate {
    val hasGoogleServices = googleServicesFile.exists()
    
    extensions.findByType(CrashlyticsExtension::class.java)?.apply {
        mappingFileUploadEnabled = hasGoogleServices
        nativeSymbolUploadEnabled = hasGoogleServices
    }
    
    // Disable Crashlytics upload tasks if google-services.json doesn't exist
    // Using enabled = false prevents task configuration validation errors
    tasks.matching { it.name.startsWith("uploadCrashlytics") }.configureEach {
        enabled = hasGoogleServices
    }
}

gradle.projectsEvaluated {
    tasks.withType<com.google.gms.googleservices.GoogleServicesTask>().configureEach {
        onlyIf { googleServicesFile.exists() }
    }
}