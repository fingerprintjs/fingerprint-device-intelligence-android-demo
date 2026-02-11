package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

import android.Manifest
import junit.framework.TestCase
import org.junit.Test

class CheckPermissionUseCaseUnitTests {

    @Test
    fun givenLocationPermissionGranted_whenIsAnyLocationPermissionGranted_thenReturnsTrue() {
        val permissionChecker = FakePermissionChecker(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(permissionChecker)

        val result = useCase.isAnyLocationPermissionGranted()

        TestCase.assertTrue(result)
    }

    @Test
    fun givenLocationPermissionNotGranted_whenIsAnyLocationPermissionGranted_thenReturnsFalse() {
        val permissionChecker = FakePermissionChecker(hasLocationPermission = false)
        val useCase = CheckPermissionUseCase(permissionChecker)

        val result = useCase.isAnyLocationPermissionGranted()

        TestCase.assertFalse(result)
    }

    @Test
    fun givenFineLocationPermissionGranted_whenIsPermissionGranted_thenReturnsTrue() {
        val permissionChecker = FakePermissionChecker(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(permissionChecker)

        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        TestCase.assertTrue(result)
    }

    @Test
    fun givenCoarseLocationPermissionGranted_whenIsPermissionGranted_thenReturnsTrue() {
        val permissionChecker = FakePermissionChecker(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(permissionChecker)

        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

        TestCase.assertTrue(result)
    }

    @Test
    fun givenPermissionNotGranted_whenIsPermissionGranted_thenReturnsFalse() {
        val permissionChecker = FakePermissionChecker(hasLocationPermission = false)
        val useCase = CheckPermissionUseCase(permissionChecker)

        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        TestCase.assertFalse(result)
    }

    private class FakePermissionChecker(
        private val hasLocationPermission: Boolean,
    ) : PermissionChecker {

        override fun isPermissionGranted(permission: String): Boolean {
            return when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                -> hasLocationPermission
                else -> false
            }
        }

        override fun isAnyLocationPermissionGranted(): Boolean = hasLocationPermission
    }
}
