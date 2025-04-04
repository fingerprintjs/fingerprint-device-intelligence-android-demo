package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.compose.runtime.Stable
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.UiError
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty

@Stable
data class HomeScreenUiState(
    val reload: ReloadState,
    val appBar: AppBarState,
    val mocking: MockingState?,
    val content: Content,
) {

    @Stable
    data class MockingState(
        val enabled: Boolean,
        val onToggle: () -> Unit,
    )

    @Stable
    data class ReloadState(
        val reloadAllowed: Boolean,
        val onReload: () -> Unit,
    )

    @Stable
    data class AppBarState(
        val onSupportClicked: () -> Unit,
        val onDocumentationClicked: () -> Unit,
        val onSignupClicked: () -> Unit,
    )

    sealed interface Content {
        @Stable
        data class TapToBegin(
            val onTap: () -> Unit,
        ) : Content {
            companion object
        }

        @Stable
        data class LoadingOrSuccess(
            val visitorId: String,
            val isLoading: Boolean,
            val isSignupPromptShown: Boolean,
            val rawJson: String?,
            val prettifiedProps: List<PrettifiedProperty>,
            val onSignupPromptClicked: () -> Unit,
            val onHideSignupPromptClicked: () -> Unit,
        ) : Content {
            companion object
        }

        @Stable
        data class Error(
            val error: UiError,
            val onBtnCLick: () -> Unit,
        ) : Content {
            companion object
        }

        companion object
    }

    companion object
}
