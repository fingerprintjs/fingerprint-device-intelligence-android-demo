<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="resources/logo_light.svg" />
      <source media="(prefers-color-scheme: light)" srcset="resources/logo_dark.svg" />
      <img src="resources/logo_dark.svg" alt="Fingerprint logo" width="312px" />
    </picture>
</p>

<p align="center">
  <a href="https://discord.gg/39EpE2neBg">
    <img src="https://img.shields.io/discord/852099967190433792?style=logo&label=Discord&logo=Discord&logoColor=white" alt="Discord server">
  </a>
    <a href="https://android-arsenal.com/api?level=21">
    <img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg" alt="Android minAPI status">
  </a>
  <a href="https://github.com/fingerprintjs/fingerprintjs-pro-android-demo/releases/tag/v2.8.0">
    <img src="https://img.shields.io/badge/SDK-2.8.0-orange" alt="Fingerprint Identification SDK version">
  </a>
</p>

[Fingerprint’s Device Intelligence platform for Android](https://dev.fingerprint.com/docs/native-android-integration) helps you to accurately identify the devices on which your mobile app is being used. The platform also provides high-quality [Smart Signals](https://dev.fingerprint.com/docs/smart-signals-overview#smart-signals-for-mobile-devices) that will help you identify risky transactions before they happen. The Fingerprint Pro Demo App for Android allows you to effortlessly experience the capabilities of our device intelligence platform.

# Fingerprint Pro Demo App in Google Play

<p align="center">
 	<a href='https://play.google.com/store/apps/details?id=com.fingerprintjs.android.fpjs_pro_demo'>
 		<img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' width="240px"/>
 	</a>
 </p>

<p align="center">
  <img src="resources/fingerprint-demo-1.gif" width="195">
  <img src="resources/fingerprint-demo-2.webp" width="195">
  <img src="resources/fingerprint-demo-3.webp" width="195">
  <img src="resources/fingerprint-demo-4.webp" width="195">
</p>


# About

This repository contains the source code for the Fingerprint Pro Demo App for Android. It shall serve as a good example

- For integrating the Fingerprint Identification SDK in your Android app, complimenting our [Getting Started Guide](https://dev.fingerprint.com/docs/android-sdk);
- For best practices to follow when using the Fingerprint Identification SDK in your app.

# Getting started
## Install the app

You can install the app using one of the following methods:
1. Install from [Google Play](https://play.google.com/store/apps/details?id=com.fingerprintjs.android.fpjs_pro_demo)
2. Download and install .apk file from the [Releases page](https://github.com/fingerprintjs/fingerprint-device-intelligence-android-demo/releases)
3. [Build the app from sources](#build-the-app-from-sources)

## Build the app from sources

Build the app from sources in a few simple steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/fingerprintjs/fingerprint-device-intelligence-android-demo.git
    ```
2. Open the cloned repository in Android Studio.
3. In file `app/src/main/java/com/fingerprintjs/android/fpjs_pro_demo/constants/Credentials.kt`,
   * Replace the value for `apiKey` with your Public API Key. You can find the Public API Key in your [dashboard under API Keys](https://dashboard.fingerprint.com/api-keys).

     ```kotlin
     import com.fingerprintjs.android.fpjs_pro.Configuration

     object Credentials {
       val apiKey: String = "YOUR_PUBLIC_API_KEY"
     }
     ```
   * If you **do not** have a custom sub-domain or a proxy integration, then the value of `endpointUrl` should match the [region](https://dev.fingerprint.com/docs/android-sdk#region-1) you specified when registering your app with Fingerprint.

     ```kotlin
     import com.fingerprintjs.android.fpjs_pro.Configuration

     object Credentials {
       val endpointUrl: String = Configuration.Region.US.endpointUrl
     }
     ```
   * If you have set up a have a custom sub-domain or a proxy integration, then the value of `endpointUrl` should represent the endpoint through which you want to route the requests.

     ```kotlin
     import com.fingerprintjs.android.fpjs_pro.Configuration

     object Credentials {
       val endpointUrl: String = "https://fingerprint.example.com"
     }
     ```
4. In the "Build Variants" tool window, choose a build variant that suits your needs. All the available build variants are listed under `buildTypes{...}` in file [app/build.gradle.kts](app/build.gradle.kts)
   
   :bulb: The `debug` and `debugOptimized` variants of the app include an icon that allows you to iteratively build the UI without making an actual request to our Fingerprint servers. And save API calls!
6. Run the app on the selected device



# Fingerprint Identification SDK

Following up on the information provided in our [Getting Started Guide](https://dev.fingerprint.com/docs/android-sdk), you might refer to this repository for an example of:

## Getting the response

* The SDK configuration code is located at the [`di` package](app/src/main/java/com/fingerprintjs/android/fpjs_pro_demo/di) of the app module. 
* The method `getVisitorId()` method is abstracted in [IdentificationProvider](app/src/main/java/com/fingerprintjs/android/fpjs_pro_demo/domain/identification/IdentificationProvider.kt) class.

## Examining the response

The method `IdentificationProvider.getVisitorId()` is called in the file [HomeViewModel.kt](app/src/main/java/com/fingerprintjs/android/fpjs_pro_demo/ui/screens/home/viewmodel/HomeViewModel.kt) file. The result is displayed by the [EventDetailsView](app/src/main/java/com/fingerprintjs/android/fpjs_pro_demo/ui/screens/home/views/event_details_view) composable either in prettified or raw format.


# License

The source code in this repository is licensed under the [MIT license](LICENSE).
