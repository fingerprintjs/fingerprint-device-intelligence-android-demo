package com.fingerprintjs.android.fpjs_pro_demo

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.navigation.NavScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.ALPHA,
                    1f,
                    0f
                )
                slideUp.interpolator = AccelerateInterpolator()
                slideUp.duration = 200L
                slideUp.doOnEnd { splashScreenView.remove() }
                slideUp.start()
            }
        }

        setContent {
            AppTheme {
                NavScreen()
            }
        }

        checkLocationPermissions()
    }

    fun checkLocationPermissions() {
        if (isAnyLocationPermissionGranted()) {
            return
        }

        try {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } catch (e: IllegalStateException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun isAnyLocationPermissionGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) ||
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
