package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.andThenRecover
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.runCatching

object IntentUtils {
    fun openUrl(context: Context, url: String) {
        openUrlInternal(context, url)
            .onFailure {
                Toast.makeText(
                    context,
                    "Error opening URL",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    fun openAppInPlayStore(context: Context) {
        playStoreCurrentAppUrl(context)
            .andThen { url ->
                runCatching {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(url)
                        setPackage("com.android.vending")
                    }
                    context.startActivity(intent)
                }
                    .andThenRecover { openUrlInternal(context, url) }
            }
            .onFailure {
                Toast.makeText(
                    context,
                    "Error opening Play Store page",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun openUrlInternal(context: Context, url: String): Result<*, Throwable> {
        return runCatching {
            val uri = Uri.parse(url)!!
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private fun playStoreCurrentAppUrl(context: Context): Result<String, *> {
        return runCatching {
            Uri.parse(URLs.playStoreListing)
                .buildUpon()
                .appendQueryParameter("id", context.packageName)
                .toString()
        }
    }
}
