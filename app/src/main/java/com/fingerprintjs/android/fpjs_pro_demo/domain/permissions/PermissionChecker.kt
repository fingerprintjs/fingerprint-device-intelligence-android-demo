package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

/**
 * Abstraction for runtime permission checks. Allows testing without Android Context.
 */
interface PermissionChecker {

    fun isPermissionGranted(permission: String): Boolean

    fun isAnyLocationPermissionGranted(): Boolean
}
