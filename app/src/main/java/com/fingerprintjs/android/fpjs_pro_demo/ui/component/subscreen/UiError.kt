package com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.ui.util.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.ui.util.ShowPreview


@Suppress("MaxLineLength")
sealed class UiError {
    abstract val image: ImageVector
    abstract val title: String
    abstract val description: String
    abstract val linkMasks: List<String>
    abstract val buttonTitle: String

    data object PublicApiKeyExpired : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to Fingerprint"
        override val description = "The public key has expired."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object PublicApiKeyInvalid : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to Fingerprint"
        override val description =
            "The public API key is missing or invalid. Ensure the key was entered correctly."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object SubscriptionNotActive : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to Fingerprint"
        override val description = "The application is not active for the provided public API key."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object WrongRegion : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to Fingerprint"
        override val description =
            "The public API key is not intended for the selected region. Visit Settings to change the region."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object SecretApiKeyMismatch : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to fetch Smart Signals"
        override val description =
            "The provided secret API key is invalid. Make sure that provided public and secret API keys belong to the same application."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object SecretApiKeyInvalid : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "Failed to fetch Smart Signals"
        override val description =
            "The provided secret API key is either missing or invalid. Please double-check that the key was entered correctly."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Go to API Keys"
    }

    data object Unknown : UiError() {
        override val image = Icons.Outlined.ErrorOutline
        override val title = "An unexpected error occurred..."
        override val description = " Please contact support if this issue persists."
        override val linkMasks = listOf("contact support")
        override val buttonTitle = "Try again"
    }

    data object Network : UiError() {
        override val image = Icons.Outlined.CloudOff
        override val title = "Server cannot be reached"
        override val description = "Please check your network settings and try again."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Try again"
    }

    data object TooManyRequests : UiError() {
        override val image = Icons.Outlined.BackHand
        override val title = "Too many requests"
        override val description =
            " The request rate limit set for the public API key was exceeded."
        override val linkMasks = emptyList<String>()
        override val buttonTitle = "Try again"
    }
}

private const val TOP_SPACER_WEIGHT = 0.34f
private const val CONTENT_WEIGHT = 0.66f

@Composable
fun UiError(
    modifier: Modifier,
    error: UiError,
    onBtnClicked: () -> Unit,
    onLinkClicked: () -> Unit,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.weight(TOP_SPACER_WEIGHT))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(CONTENT_WEIGHT),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp),
                imageVector = error.image,
                contentDescription = "Error icon",
                colorFilter = ColorFilter.tint(color = AppTheme.materialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                style = AppTheme.materialTheme.typography.titleLarge,
                color = AppTheme.materialTheme.colorScheme.onBackground,
                text = error.title,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            val description = remember(error.description) {
                buildAnnotatedString { append(error.description) }
            }
            LinkableText(
                text = description,
                links = makeLinks(error, onLinkClicked),
                style = AppTheme.materialTheme.typography.bodyLarge,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onBtnClicked,
            ) {
                Text(error.buttonTitle)
            }
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    ShowPreview {
        Surface {
            UiError(
                modifier = Modifier.fillMaxSize(),
                error = UiError.Unknown,
                onBtnClicked = {},
                onLinkClicked = {},
            )
        }
    }
}

private fun makeLinks(error: UiError, onLinkClick: () -> Unit): List<LinkableText.Link> =
    when (error) {
        UiError.Unknown -> listOf(
            LinkableText.Link(mask = error.linkMasks[0], handler = onLinkClick)
        )

        else -> emptyList()
    }
