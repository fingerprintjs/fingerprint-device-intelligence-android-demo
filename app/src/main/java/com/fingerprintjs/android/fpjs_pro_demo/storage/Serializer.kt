package com.fingerprintjs.android.fpjs_pro_demo.storage

import com.github.michaelbull.result.Result

interface Serializer {
    suspend fun serialize(data: Any): Result<ByteArray, *>
    suspend fun <T> deserialize(data: ByteArray, classOfT: Class<T>): Result<T, *>
}
