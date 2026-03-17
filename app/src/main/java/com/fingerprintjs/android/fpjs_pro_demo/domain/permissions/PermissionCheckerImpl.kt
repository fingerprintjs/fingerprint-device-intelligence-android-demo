package com.fingerprintjs.android.fpjs_pro_demo.domain.permissions

import android.content.Context
import com.fingerprintjs.android.fpjs_pro_demo.utils.PermissionUtils
import javax.inject.Inject

class PermissionCheckerImpl @Inject constructor(
    private val context: Context,
) : PermissionChecker {

    override fun isPermissionGranted(permission: String): Boolean {
        return PermissionUtils.isPermissionGranted(context, permission)
    }

    override fun isAnyLocationPermissionGranted(): Boolean {
        return PermissionUtils.isAnyLocationPermissionGranted(context)
    }
}
