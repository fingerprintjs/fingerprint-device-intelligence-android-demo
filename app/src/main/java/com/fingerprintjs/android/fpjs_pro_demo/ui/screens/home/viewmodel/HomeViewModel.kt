package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory
import com.fingerprintjs.android.fpjs_pro_demo.BuildConfig
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.utils.FingerprintJSWrappedResult
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import com.fingerprintjs.android.fpjs_pro_demo.utils.getVisitorId
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val applicationContext: Context) : ViewModel() {
    private val mutableState = MutableStateFlow<HomeViewModelState>(HomeViewModelState.TapToBegin)
    val state: StateFlow<HomeViewModelState> = mutableState

    private val mockingMutableState = MutableStateFlow(if (BuildConfig.ALLOW_MOCKS) false else null)
    val mockingState: StateFlow<Boolean?> = mockingMutableState

    private val externalLinkToOpenMutable = MutableSharedFlow<String>()
    val externalLinkToOpen: Flow<String>
        get() = externalLinkToOpenMutable

    private var fingerprinter: FingerprintJS? = null
    private var initJob: Job

    private val loadingState = (HomeViewModelState.from(
        FingerprintJSWrappedResult.from(
            StateMocks.fingerprintJSResponse
        )
    ) as HomeViewModelState.LoadingOrSuccess).copy(isLoading = true)

    init {
        initJob = viewModelScope.launch {
            fingerprinter = FingerprintJSFactory(applicationContext).createInstance(
                Configuration(
                    apiKey = Credentials.apiKey,
                    endpointUrl = Credentials.endpointUrl,
                    extendedResponseFormat = true,
                )
            )
        }
    }

    private var reloadCount = 0

    fun onStartFingerprint() = onReloadFingerprint()

    fun onReloadFingerprint() {
        viewModelScope.launch {
            mutableState.value = loadingState
            delay(2000)
            mutableState.value = HomeViewModelState.from(getVisitorId())
        }
    }

    fun onSupportClicked() = launchUrl(URLs.support)
    fun onDocumentationClicked() = launchUrl(URLs.documentation)

    fun onToggleMocking() {
        mockingMutableState.value?.let {
            mockingMutableState.value = !it
        }
    }

    private fun launchUrl(url: String) {
        viewModelScope.launch { externalLinkToOpenMutable.emit(url) }
    }

    private suspend fun getVisitorId(): FingerprintJSWrappedResult {
        initJob.join()
        return if (mockingState.value == true) {
            if (++reloadCount % 4 == 0)
                FingerprintJSWrappedResult.Error(
                    error = StateMocks.fingerprintJSOtherError
                )
            else FingerprintJSWrappedResult.Success(
                result = StateMocks.fingerprintJSResponse
            )
        } else {
            fingerprinter!!.getVisitorId()
        }
    }
}
