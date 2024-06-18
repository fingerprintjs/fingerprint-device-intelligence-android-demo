package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty

sealed interface HomeScreenUiState {

    val reloadAllowed: Boolean
    val onReload: () -> Unit
    val onSupportClicked: () -> Unit
    val onDocumentationClicked: () -> Unit
    val onSignupClicked: () -> Unit

    @Stable
    data class TapToBegin(
        val onTap: () -> Unit,
        override val onReload: () -> Unit,
        override val onSupportClicked: () -> Unit,
        override val onDocumentationClicked: () -> Unit,
        override val onSignupClicked: () -> Unit,
    ) : HomeScreenUiState {

        override val reloadAllowed: Boolean
            get() = false

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
        override val onReload: () -> Unit,
        override val onSupportClicked: () -> Unit,
        override val onDocumentationClicked: () -> Unit,
        override val onSignupClicked: () -> Unit,
    ) : HomeScreenUiState {

        override val reloadAllowed: Boolean
            get() = !isLoading

        companion object
    }

    @Stable
    data class Error(
        val image: ImageVector,
        val title: String,
        val description: String,
        val links: List<LinkableText.Link>,
        val onButtonCLick: () -> Unit,
        override val onReload: () -> Unit,
        override val onSupportClicked: () -> Unit,
        override val onDocumentationClicked: () -> Unit,
        override val onSignupClicked: () -> Unit,
    ): HomeScreenUiState {

        override val reloadAllowed: Boolean
            get() = true

        companion object
    }

    companion object
}
