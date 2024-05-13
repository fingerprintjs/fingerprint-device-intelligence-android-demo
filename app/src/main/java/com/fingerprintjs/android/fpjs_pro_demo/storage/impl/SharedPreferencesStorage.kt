package com.fingerprintjs.android.fpjs_pro_demo.storage.impl

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.fingerprintjs.android.fpjs_pro_demo.storage.BackingStorage
import com.fingerprintjs.android.fpjs_pro_demo.storage.StorageKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SharedPreferencesStorage @Inject constructor(
    context: Context,
): BackingStorage {

    private val sharedPrefsFileNameHistory = listOf(
        "fpjs_prefs", // old shared prefs
        "fpjs_prefs_v1", // current shared prefs
    )

    private val sharedPreferences : SharedPreferences

    init {
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            sharedPrefsFileNameHistory.dropLast(1)
                .forEach {
                    runCatching { context.deleteSharedPreferences(it) }
                }
        }

        sharedPreferences = context.getSharedPreferences(
            sharedPrefsFileNameHistory.last(),
            Context.MODE_PRIVATE
        )!!
    }


    override suspend fun writeData(bytes: ByteArray, key: StorageKey): Result<*, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                sharedPreferences.edit().putString(
                    key.representation,
                    Base64.encodeToString(bytes, Base64.DEFAULT)
                ).commit()
            }
        }
    }

    override suspend fun readData(key: StorageKey): Result<ByteArray, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val data = sharedPreferences.getString(
                    key.representation,
                    null
                )!!
                Base64.decode(data, Base64.DEFAULT)
            }
        }
    }
}
