package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.Drn
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.DrnError
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.UiError

sealed class DrnUiState {
    data object Initial : DrnUiState()

    data class Error(val error: UiError) : DrnUiState()

    data class Main(
        val loading: Boolean,
        val drn: Drn?
    ) : DrnUiState()
}

sealed class DrnUserAction {
    data object OnTapToBeginClicked : DrnUserAction()
    data object OnTryAgainClicked : DrnUserAction()
}

fun DrnError.mapToUi(): UiError = when (this) {
    is DrnError.NetworkError -> UiError.Network
    DrnError.Unknown,
    DrnError.ApiError.UnknownApiError,
    DrnError.ApiError.VisitorNotFound,
    DrnError.EndpointInfoNotSetInApp,
    DrnError.ParseError -> UiError.Unknown
}
