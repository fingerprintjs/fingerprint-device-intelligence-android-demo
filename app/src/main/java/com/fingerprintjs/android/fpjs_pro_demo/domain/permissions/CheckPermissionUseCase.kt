package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

import android.content.Context
import com.fingerprintjs.android.fpjs_pro_demo.utils.PermissionUtils
import javax.inject.Inject

class CheckPermissionUseCase @Inject constructor(
    private val context: Context,
) {
    /**
     * Checks if a specific permission is granted.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return PermissionUtils.isPermissionGranted(context, permission)
    }

    /**
     * Checks if any location permission (fine or coarse) is granted.
     * Convenience method for the common location permission check.
     */
    fun isAnyLocationPermissionGranted(): Boolean {
        return PermissionUtils.isAnyLocationPermissionGranted(context)
    }
}
