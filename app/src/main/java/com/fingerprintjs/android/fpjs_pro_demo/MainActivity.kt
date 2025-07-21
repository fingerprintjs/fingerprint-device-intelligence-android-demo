package com.fingerprintjs.android.fpjs_pro_demo

import android.Manifest
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.IdentificationProvider
import com.fingerprintjs.android.fpjs_pro_demo.ui.navigation.NavScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var identificationProvider: IdentificationProvider
    private var locationGatheringPermitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).appComponent.inject(this)
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
                slideUp.doOnEnd {
                    splashScreenView.remove()
                    checkLocationPermissions()
                }
                slideUp.start()
            }
        } else {
            checkLocationPermissions()
        }

        setContent {
            AppTheme {
                NavScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (locationGatheringPermitted) {
            identificationProvider.startGatheringDeviceIntelligence()
        }
    }

    override fun onPause() {
        identificationProvider.stopGatheringDeviceIntelligence()
        super.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (isAnyLocationPermissionGranted()) {
                startLocationCollection()
            } else if (isRationaleNeeded()) {
                showRationaleDialog()
            }
        }
    }

    private fun checkLocationPermissions() {
        if (isFinePermissionGranted()) {
            startLocationCollection()
        } else {
            if (isRationaleNeeded()) {
                showRationaleDialog()
            } else {
                requestLocationPermissions()
            }
        }
    }

    private fun isFinePermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun isAnyLocationPermissionGranted(): Boolean {
        return listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).any { ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun isRationaleNeeded(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location permissions needed")
            .setMessage("Precise location permission needed, tap \"While using the app\" on the next screen")
            .setPositiveButton(
                "OK"
            ) { _, _ ->
                requestLocationPermissions()
            }
            .create()
            .show()
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            LOCATION_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun startLocationCollection() {
        locationGatheringPermitted = true
        identificationProvider.startGatheringDeviceIntelligence()
    }

    companion object {
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1843
    }
}
