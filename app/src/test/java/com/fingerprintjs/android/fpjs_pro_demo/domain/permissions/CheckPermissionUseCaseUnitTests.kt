package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

import android.Manifest
import android.content.Context
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
class CheckPermissionUseCaseUnitTests {

    @Test
    fun givenLocationPermissionGranted_whenIsAnyLocationPermissionGranted_thenReturnsTrue() {
        // given
        val context = createMockContext(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(context)

        // when
        val result = useCase.isAnyLocationPermissionGranted()

        // then
        TestCase.assertTrue(result)
    }

    @Test
    fun givenLocationPermissionNotGranted_whenIsAnyLocationPermissionGranted_thenReturnsFalse() {
        // given
        val context = createMockContext(hasLocationPermission = false)
        val useCase = CheckPermissionUseCase(context)

        // when
        val result = useCase.isAnyLocationPermissionGranted()

        // then
        TestCase.assertFalse(result)
    }

    @Test
    fun givenFineLocationPermissionGranted_whenIsPermissionGranted_thenReturnsTrue() {
        // given
        val context = createMockContext(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(context)

        // when
        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        // then
        TestCase.assertTrue(result)
    }

    @Test
    fun givenCoarseLocationPermissionGranted_whenIsPermissionGranted_thenReturnsTrue() {
        // given
        val context = createMockContext(hasLocationPermission = true)
        val useCase = CheckPermissionUseCase(context)

        // when
        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)

        // then
        TestCase.assertTrue(result)
    }

    @Test
    fun givenPermissionNotGranted_whenIsPermissionGranted_thenReturnsFalse() {
        // given
        val context = createMockContext(hasLocationPermission = false)
        val useCase = CheckPermissionUseCase(context)

        // when
        val result = useCase.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

        // then
        TestCase.assertFalse(result)
    }

    private fun createMockContext(hasLocationPermission: Boolean = false): Context {
        val context = RuntimeEnvironment.getApplication()
        val shadowApplication = ShadowApplication()

        if (hasLocationPermission) {
            shadowApplication.grantPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            shadowApplication.denyPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        return context
    }
}
