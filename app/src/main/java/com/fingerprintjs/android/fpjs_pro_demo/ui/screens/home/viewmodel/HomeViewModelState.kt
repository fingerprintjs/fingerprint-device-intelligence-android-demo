package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.TooManyRequest
import com.fingerprintjs.android.fpjs_pro_demo.utils.FingerprintJSWrappedResult
import com.fingerprintjs.android.fpjs_pro_demo.utils.toPrettyJson

sealed interface HomeViewModelState {
    data object TapToBegin : HomeViewModelState
    data class LoadingOrSuccess(
        val requestId: String?,
        val visitorId: String,
        val visitorFound: Boolean,
        val confidence: Double,
        val ipAddress: String?,
        val ipCity: String?,
        val ipCountry: String?,
        val firstSeenAt: String?,
        val lastSeenAt: String?,
        val rawJson: String?,
        val isLoading: Boolean,
    ) : HomeViewModelState

    sealed class Error : HomeViewModelState {
        data object Network: Error()
        data object TooManyRequests: Error()
        data object Unknown: Error()
    }

    companion object
}

val HomeViewModelState.reloadAllowed: Boolean
    get() = (this is HomeViewModelState.LoadingOrSuccess && !this.isLoading) ||
            this is HomeViewModelState.Error

fun HomeViewModelState.Companion.from(
    result: FingerprintJSWrappedResult
): HomeViewModelState {

    // Checking the values from FingerprintJSProResponse for unavailability
    // is very inconvenient now. It will be improved in the future releases of the SDK.
    fun String.dropEssentiallyEmpty(): String? = takeIf {
        it.isNotEmpty()
                && it != "n\\a"
                && !it.contentEquals("null", ignoreCase = true)
    }

    return when (result) {
        is FingerprintJSWrappedResult.Error -> when(result.error) {
            is NetworkError -> HomeViewModelState.Error.Network
            is TooManyRequest -> HomeViewModelState.Error.TooManyRequests
            else -> HomeViewModelState.Error.Unknown
        }
        is FingerprintJSWrappedResult.Success -> HomeViewModelState.LoadingOrSuccess(
            requestId = result.result.requestId.dropEssentiallyEmpty(),
            visitorId = result.result.visitorId,
            visitorFound = result.result.visitorFound,
            confidence = result.result.confidenceScore.score,
            ipAddress = result.result.ipAddress.dropEssentiallyEmpty(),
            ipCity = result.result.ipLocation?.city?.name?.dropEssentiallyEmpty(),
            ipCountry = result.result.ipLocation?.country?.name?.dropEssentiallyEmpty(),
            firstSeenAt = result.result.firstSeenAt.subscription.dropEssentiallyEmpty(),
            lastSeenAt = result.result.lastSeenAt.subscription.dropEssentiallyEmpty(),
            rawJson = runCatching {
                result.result.toPrettyJson()
            }.getOrNull(),
            isLoading = false,
        )
    }
}
