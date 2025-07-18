package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
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
            val isSmartSignalsLoading: Boolean,
            val isSignupPromptShown: Boolean,
            val rawJson: String?,
            val prettifiedProps: List<PrettifiedProperty>,
            val onSignupPromptClicked: () -> Unit,
            val onHideSignupPromptClicked: () -> Unit,
        ) : Content {
            companion object
        }

        @Stable
        sealed interface Error : Content {
            val image: ImageVector
            val title: String
            val description: String
            val links: List<LinkableText.Link>
            val buttonTitle: String
            val onButtonCLick: () -> Unit

            class PublicApiKeyExpired(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to Fingerprint"
                override val description = "The public key has expired."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class PublicApiKeyInvalid(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to Fingerprint"
                override val description = "The public API key is missing or invalid. Ensure the key was entered correctly."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class SubscriptionNotActive(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to Fingerprint"
                override val description = "The application is not active for the provided public API key."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class WrongRegion(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to Fingerprint"
                override val description = "The public API key is not intended for the selected region. Visit Settings to change the region."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class SecretApiKeyMismatch(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to fetch Smart Signals"
                override val description = "The provided secret API key is invalid. Make sure that provided public and secret API keys belong to the same application."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class SecretApiKeyInvalid(
                onGotoApiKeysSettings: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "Failed to fetch Smart Signals"
                override val description = "The provided secret API key is either missing or invalid. Please double-check that the key was entered correctly."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Go to API Keys"
                override val onButtonCLick = onGotoApiKeysSettings
            }

            class Unknown(
                onSupportClicked: () -> Unit,
                onReload: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = "An unexpected error occurred..."
                override val description = " Please contact support if this issue persists."
                override val links = listOf(
                    LinkableText.Link(
                        mask = "contact support",
                        handler = onSupportClicked,
                    )
                )
                override val buttonTitle: String = "Try again"
                override val onButtonCLick = onReload
            }

            class Network(
                onReload: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.CloudOff
                override val title = "Server cannot be reached"
                override val description = "Please check your network settings and try again."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Try again"
                override val onButtonCLick = onReload
            }

            class TooManyRequests(
                onReload: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.BackHand
                override val title = "Too many requests"
                override val description = " The request rate limit set for the public API key was exceeded."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Try again"
                override val onButtonCLick = onReload
            }

            class Generic(
                error: com.fingerprintjs.android.fpjs_pro.Error,
                onReload: () -> Unit,
            ) : Error {
                override val image = Icons.Outlined.ErrorOutline
                override val title = error.javaClass.simpleName
                override val description = error.description ?: "An unexpected error occurred..."
                override val links = emptyList<LinkableText.Link>()
                override val buttonTitle: String = "Try again"
                override val onButtonCLick = onReload
            }

            companion object
        }

        companion object
    }

    companion object
}
