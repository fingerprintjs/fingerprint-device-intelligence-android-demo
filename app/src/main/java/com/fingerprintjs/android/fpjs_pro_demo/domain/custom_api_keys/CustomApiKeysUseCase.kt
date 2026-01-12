package com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys

import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import com.fingerprintjs.android.fpjs_pro_demo.storage.AppStorage
import com.fingerprintjs.android.fpjs_pro_demo.storage.StorageKey
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.get
import com.github.michaelbull.result.runCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScope
class CustomApiKeysUseCase @Inject constructor(
    private val appStorage: AppStorage,
) {
    private val _state = MutableSharedFlow<CustomApiKeysState>(replay = 1)
    val state: Flow<CustomApiKeysState> = _state.distinctUntilChanged()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            refreshState()
        }
    }

    suspend fun tryUpdate(
        newState: CustomApiKeysState,
    ): Boolean {
        if (newState.enabled && (newState.public.isBlank() || newState.secret.isBlank())) {
            return false
        }

        saveState(newState)
        refreshState()
        return true
    }

    private suspend fun refreshState() {
        val loadedState = loadState().get() ?: CustomApiKeysState.Default
        _state.emit(loadedState)
    }

    private suspend fun saveState(state: CustomApiKeysState): Result<*, *> {
        return appStorage.save(state.public, StorageKey.CustomPublicApiKey)
            .andThen { appStorage.save(state.secret, StorageKey.CustomSecretApiKey) }
            .andThen { appStorage.save(state.enabled, StorageKey.CustomApiKeysEnabled) }
            .andThen { appStorage.save(state.region.name, StorageKey.CustomApiKeysServerRegion) }
    }

    private suspend fun loadState(): Result<CustomApiKeysState, *> {
        return coroutineBinding {
            val public = appStorage.load(StorageKey.CustomPublicApiKey, String::class).bind()
            val secret = appStorage.load(StorageKey.CustomSecretApiKey, String::class).bind()
            val region = appStorage.load(StorageKey.CustomApiKeysServerRegion, String::class)
                .andThen { runCatching { Configuration.Region.valueOf(it) } }.bind()
            val enabled =
                appStorage.load(StorageKey.CustomApiKeysEnabled, Boolean::class).bind()
            CustomApiKeysState(
                public = public,
                secret = secret,
                region = region,
                enabled = enabled
            )
        }
    }
}
