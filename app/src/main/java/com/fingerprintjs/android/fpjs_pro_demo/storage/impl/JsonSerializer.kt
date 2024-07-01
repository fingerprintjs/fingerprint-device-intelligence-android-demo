package com.fingerprintjs.android.fpjs_pro_demo.storage.impl

import com.fingerprintjs.android.fpjs_pro_demo.storage.Serializer
import com.fingerprintjs.android.fpjs_pro_demo.utils.toJsonElement
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import javax.inject.Inject
import kotlin.reflect.KClass

class JsonSerializer @Inject constructor(
    private val json: Json,
) : Serializer {

    override suspend fun serialize(data: Any): Result<ByteArray, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                json.encodeToString(data.toJsonElement()).toByteArray(Charsets.UTF_8)
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> deserialize(data: ByteArray, classOfT: KClass<T>): Result<T, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                Json.decodeFromString(
                    classOfT.serializer(),
                    data.toString(Charsets.UTF_8)
                )
            }
        }
    }
}
