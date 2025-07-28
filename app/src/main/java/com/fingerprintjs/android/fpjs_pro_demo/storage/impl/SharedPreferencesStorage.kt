package com.fingerprintjs.android.fpjs_pro_demo.storage.impl

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import com.fingerprintjs.android.fpjs_pro_demo.storage.BackingStorage
import com.fingerprintjs.android.fpjs_pro_demo.storage.StorageKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AppScope
class SharedPreferencesStorage @Inject constructor(
    private val context: Context,
) : BackingStorage {
    private val encryptedSharedPreferencesSupported = android.os.Build.VERSION.SDK_INT >= 23

    private val sharedPrefsFileNameHistory = if (encryptedSharedPreferencesSupported) {
        listOf(

            "fpjs_prefs", // old shared prefs
            "fpjs_prefs_v1",
            "fpjs_prefs_v2_legacy", // if device has been updated from api levels 21-22,
            // let's delete unencrypted shared preferences
            "fpjs_prefs_v2", // current shared prefs
        )
    } else {
        listOf(
            // old shared prefs
            "fpjs_prefs",
            "fpjs_prefs_v1",
            // current shared prefs
            "fpjs_prefs_v2_legacy",
        )
    }

    init {
        GlobalScope.launch { clearPreviousSharedPreferences() }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            EncryptedSharedPreferences.create(
                sharedPrefsFileNameHistory.last(),
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences(
                sharedPrefsFileNameHistory.last(),
                Context.MODE_PRIVATE
            )!!
        }
    }

    // for now, let's not bother with migration, there is no important information to migrate
    // from the previous released version of the app
    @SuppressLint("ApplySharedPref")
    private suspend fun clearPreviousSharedPreferences() {
        withContext(Dispatchers.IO) {
            sharedPrefsFileNameHistory.dropLast(1)
                .forEach {
                    runCatching {
                        if (android.os.Build.VERSION.SDK_INT >= 24) {
                            context.deleteSharedPreferences(it)
                        } else {
                            context.getSharedPreferences(it, Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .commit()
                        }
                    }
                }
        }
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
