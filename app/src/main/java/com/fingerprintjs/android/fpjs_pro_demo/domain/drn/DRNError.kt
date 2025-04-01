package com.fingerprintjs.android.fpjs_pro_demo.domain.drn

import java.io.IOException

sealed interface DRNError {
    data object EndpointInfoNotSetInApp : DRNError
    data class NetworkError(val cause: IOException) : DRNError
    data object ParseError : DRNError
    data object Unknown : DRNError


    sealed interface APIError : DRNError
    data object VisitorNotFound : DRNError, APIError
    data object UnknownApiError : DRNError, APIError
}
