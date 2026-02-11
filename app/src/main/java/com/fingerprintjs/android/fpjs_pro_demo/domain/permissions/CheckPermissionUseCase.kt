package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

import javax.inject.Inject

class CheckPermissionUseCase @Inject constructor(
    private val permissionChecker: PermissionChecker,
) {
    /**
     * Checks if a specific permission is granted.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return permissionChecker.isPermissionGranted(permission)
    }

    /**
     * Checks if any location permission (fine or coarse) is granted.
     * Convenience method for the common location permission check.
     */
    fun isAnyLocationPermissionGranted(): Boolean {
        return permissionChecker.isAnyLocationPermissionGranted()
    }
}
