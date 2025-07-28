package com.fingerprintjs.android.fpjs_pro_demo.storage

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import javax.inject.Inject
import kotlin.reflect.KClass

class AppStorage @Inject constructor(
    private val backingStorage: BackingStorage,
    private val serializer: Serializer,
) {

    suspend fun save(data: Any, key: StorageKey): Result<*, *> {
        return serializer.serialize(data)
            .andThen { backingStorage.writeData(it, key) }
    }

    suspend fun <T : Any> load(key: StorageKey, classOfT: KClass<T>): Result<T, *> {
        return backingStorage.readData(key)
            .andThen { serializer.deserialize(it, classOfT) }
    }
}
