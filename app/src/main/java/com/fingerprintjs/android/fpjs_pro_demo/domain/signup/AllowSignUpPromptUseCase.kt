package com.fingerprintjs.android.fpjs_pro_demo.domain.signup

import com.fingerprintjs.android.fpjs_pro_demo.storage.AppStorage
import com.fingerprintjs.android.fpjs_pro_demo.storage.StorageKey
import com.github.michaelbull.result.getOrElse
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllowSignUpPromptUseCase @Inject constructor(
    private val appStorage: AppStorage,
) {
    private val _showAllowed = MutableSharedFlow<Boolean>(replay = 1)
    private val initialized = AtomicBoolean(false)

    fun showAllowed(scope: CoroutineScope): Flow<Boolean> {
        if (initialized.compareAndSet(false, true)) {
            scope.launch(Dispatchers.IO) {
                updateState()
            }
        }
        return _showAllowed
    }

    private suspend fun getFingerprintSuccessCount() =
        appStorage.load(StorageKey.FingerprintSuccessCount, Int::class).getOrElse { 0 }

    private suspend fun setFingerprintSuccessCount(value: Int) =
        appStorage.save(value, StorageKey.FingerprintSuccessCount)

    private suspend fun getSignupPromptHideTimeMillis() =
        appStorage.load(StorageKey.SignupPromptHideTimeMillis, Long::class).getOrElse { 0 }

    private suspend fun setSignupPromptHideTimeMillis(value: Long) =
        appStorage.save(value, StorageKey.SignupPromptHideTimeMillis)

    suspend fun onFingerprintSuccess() {
        setFingerprintSuccessCount(getFingerprintSuccessCount() + 1)
        updateState()
    }

    suspend fun onHideRequested() {
        setSignupPromptHideTimeMillis(System.currentTimeMillis())
        updateState()
    }

    private suspend fun updateState() {
        _showAllowed.emit(
            (System.currentTimeMillis() - getSignupPromptHideTimeMillis() > MILLIS_IN_WEEK) &&
                getFingerprintSuccessCount() >= 2
        )
    }

    companion object {
        private const val MILLIS_IN_WEEK = 1000 * 60 * 60 * 24 * 7
    }
}
