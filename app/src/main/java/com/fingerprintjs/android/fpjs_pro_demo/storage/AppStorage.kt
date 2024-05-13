package com.fingerprintjs.android.fpjs_pro_demo.storage

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import javax.inject.Inject

class AppStorage @Inject constructor(
    private val backingStorage: BackingStorage,
    private val serializer: Serializer,
)  {

    suspend fun save(data: Any, key: StorageKey): Result<*, *> {
        return serializer.serialize(data)
            .andThen { backingStorage.writeData(it, key) }
    }

    suspend fun <T> load(key: StorageKey, classOfT: Class<T>) : Result<T, *> {
        return backingStorage.readData(key)
            .andThen { serializer.deserialize(it, classOfT) }
    }
}
