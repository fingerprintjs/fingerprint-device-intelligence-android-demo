package com.fingerprintjs.android.fpjs_pro_demo.storage.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fingerprintjs.android.fpjs_pro_demo.storage.Serializer
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class JacksonSerializer @Inject constructor(
    private val objectMapper: ObjectMapper,
): Serializer {

    override suspend fun serialize(data: Any): Result<ByteArray, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                objectMapper.writeValueAsString(data).toByteArray(Charsets.UTF_8)
            }
        }
    }

    override suspend fun <T> deserialize(data: ByteArray, classOfT: Class<T>): Result<T, *> {
        return withContext(Dispatchers.IO) {
            runCatching {
                objectMapper.readValue(data.toString(Charsets.UTF_8), classOfT)
            }
        }
    }
}
