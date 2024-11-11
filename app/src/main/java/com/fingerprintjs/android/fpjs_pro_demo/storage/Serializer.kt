package com.fingerprintjs.android.fpjs_pro_demo.storage

import com.github.michaelbull.result.Result
import kotlin.reflect.KClass

interface Serializer {
    suspend fun serialize(data: Any): Result<ByteArray, *>
    suspend fun <T: Any> deserialize(data: ByteArray, classOfT: KClass<T>): Result<T, *>
}
