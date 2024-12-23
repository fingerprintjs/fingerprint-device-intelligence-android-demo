import com.android.build.gradle.internal.api.BaseVariantOutputImpl

@Suppress("PropertyName")
val VERSION_NAME="3.2.1"
@Suppress("PropertyName")
val VERSION_CODE=27
@Suppress("PropertyName")
val SDK_VERSION_NAME="2.7.0"

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
            keyAlias  = System.getenv("RELEASE_SIGN_KEY_ALIAS")
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ALLOW_MOCKS", "false")
        }
        // Use this build variant for testing the app locally with minification enabled and release
        // level of performance, but also with the mocking functionality that the debug build type has.
        create("debugOptimized") {
            isMinifyEnabled = true
            proguardFiles (getDefaultProguardFile ("proguard-android-optimize.txt"), "proguard-rules.pro")
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
            (this as? BaseVariantOutputImpl)?.outputFileName = "FPJS-Pro-Playground-${variant.name}-${variant.versionName}.apk"
        }
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.8.3")
    val useFpProDebugVersion = false // switch to true when needed to debug the locally built library
    implementation("com.fingerprint.android:pro:$SDK_VERSION_NAME${if (useFpProDebugVersion) "-debug" else ""}")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation ("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.compose.animation:animation:1.7.4")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")
    implementation("com.michael-bull.kotlin-result:kotlin-result-coroutines:2.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("androidx.security:security-crypto:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    val daggerVersion = "2.51.1"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}
