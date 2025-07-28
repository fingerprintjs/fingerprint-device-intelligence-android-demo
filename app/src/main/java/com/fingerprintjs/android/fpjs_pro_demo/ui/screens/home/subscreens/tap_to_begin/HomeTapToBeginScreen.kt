package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.tap_to_begin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.BuildConfig
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.fingerprint_button.FingerprintButton
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations

@Composable
fun HomeTapToBeginScreen(
    modifier: Modifier,
    onTapToBegin: () -> Unit,
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Tap to begin",
            textAlign = TextAlign.Center,
            style = AppTheme.materialTheme.typography.headlineMedium,
            color = AppTheme.materialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(32.dp))
        FingerprintButton(
            onClick = onTapToBegin,
            modifier = Modifier
                .size(136.dp)
                .align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "Device intelligence powered by\n" +
                "Fingerprint Android SDK v${BuildConfig.SDK_VERSION_NAME}",
            textAlign = TextAlign.Center,
            style = AppTheme.materialTheme.typography.bodyLarge,
            color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(64.dp))
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            HomeTapToBeginScreen(
                modifier = Modifier.fillMaxSize(),
                onTapToBegin = {},
            )
        }
    }
}
