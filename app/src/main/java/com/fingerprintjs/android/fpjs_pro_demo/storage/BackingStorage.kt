package com.fingerprintjs.android.fpjs_pro_demo.storage

import com.github.michaelbull.result.Result

interface BackingStorage {
    suspend fun writeData(bytes: ByteArray, key: StorageKey): Result<*, *>
    suspend fun readData(key: StorageKey): Result<ByteArray, *>
}
