package com.fingerprintjs.android.fpjs_pro_demo.domain.drn

import java.io.IOException

sealed interface DrnError {
    data object EndpointInfoNotSetInApp : DrnError
    data class NetworkError(val cause: IOException) : DrnError
    data object ParseError : DrnError
    data object Unknown : DrnError

    sealed interface ApiError : DrnError {
        data object VisitorNotFound : ApiError
        data object UnknownApiError : ApiError
    }
}
