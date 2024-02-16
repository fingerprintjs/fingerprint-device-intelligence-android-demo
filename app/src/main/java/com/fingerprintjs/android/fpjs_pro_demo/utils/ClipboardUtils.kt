package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat

object ClipboardUtils {
    fun copyToClipboardAndNotifyUser(context: Context, payload: String) {
        try {
            val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)!!
            clipboardManager.setPrimaryClip(ClipData.newPlainText("", payload))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, "Copied: $payload", Toast.LENGTH_SHORT).show()
            }
        } catch (_: Throwable) {
            Toast.makeText(context, "Copying failed", Toast.LENGTH_SHORT).show()
        }
    }
}
