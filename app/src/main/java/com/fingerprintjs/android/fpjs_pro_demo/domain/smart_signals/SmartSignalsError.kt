package com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals

import java.io.IOException

sealed interface SmartSignalsError {
    data object EndpointInfoNotSetInApp : SmartSignalsError
    data class NetworkError(val cause: IOException) : SmartSignalsError
    data object ParseError : SmartSignalsError
    data object Unknown : SmartSignalsError

    sealed interface APIError : SmartSignalsError
    data object FeatureNotEnabled : SmartSignalsError, APIError
    data object RequestNotFound : SmartSignalsError, APIError
    data object SubscriptionNotActive : SmartSignalsError, APIError
    data object TokenNotFound : SmartSignalsError, APIError
    data object TokenRequired : SmartSignalsError, APIError
    data object WrongRegion : SmartSignalsError, APIError
    data object UnknownApiError : SmartSignalsError, APIError
}
