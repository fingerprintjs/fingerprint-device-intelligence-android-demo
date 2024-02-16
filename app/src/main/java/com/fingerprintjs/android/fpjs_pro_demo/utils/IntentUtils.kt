package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object IntentUtils {
    fun openUrl(context: Context, url: String) {
        try {
            val uri = Uri.parse(url)!!
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Throwable) {
            Toast.makeText(
                context,
                "Error opening URL",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
