@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)

package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.main

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.BuildConfig
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.animatedAppBar
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.IntentUtils

@Composable
fun SettingsScreen(
    onGoToDetails: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    val viewModel = injectedViewModel { settingsViewModel }
    val state by viewModel.state.collectAsState()

    SettingsScreenInternal(state, onGoToDetails, modifier, sharedTransitionScope, animatedContentScope)
}

@Composable
private fun SettingsScreenInternal(
    state: SettingsUiState,
    onGoToDetails: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .animatedAppBar(sharedTransitionScope, animatedContentScope),
                colors = TopAppBarDefaults.topAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Settings")
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
        ) {
            val context = LocalContext.current
            SettingsEntry(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = Icons.Outlined.VpnKey,
                text = "API Keys",
                supportingText = when (state.customKeysEnabled) {
                    true -> "Enabled"
                    false -> "Disabled"
                },
                onClick = onGoToDetails,
            )
            SettingsSeparator()
            SettingsEntry(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = Icons.Outlined.StarOutline,
                text = "Write a review",
                supportingText = null,
                onClick = { IntentUtils.openAppInPlayStore(context) },
            )
            SettingsEntry(
                modifier = Modifier
                    .fillMaxWidth(),
                icon = Icons.Outlined.BackHand,
                text = "Privacy policy",
                supportingText = null,
                onClick = { IntentUtils.openUrl(context, URLs.privacyPolicy) },
            )
            SettingsSeparator()
            AppVersionsInfo()
        }
    }
}

@Composable
private fun SettingsEntry(
    modifier: Modifier,
    icon: ImageVector,
    text: String,
    supportingText: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick.invoke() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(24.dp),
            imageVector = icon,
            contentDescription = text,
            tint = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
        )
        Column(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 16.dp,
                    end = 24.dp,
                )
                .height(IntrinsicSize.Max)
                .weight(1f),
        ) {
            Text(
                style = AppTheme.materialTheme.typography.bodyLarge,
                text = text
            )
            if (supportingText != null) {
                Text(
                    style = AppTheme.materialTheme.typography.bodyMedium,
                    color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                    text = supportingText,
                )
            }
        }
    }
}

@Composable
private fun AppVersionsInfo() {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        style = AppTheme.materialTheme.typography.bodySmall,
        color = AppTheme.materialTheme.colorScheme.surfaceTint,
        text = "App v${BuildConfig.VERSION_NAME} · Fingerprint SDK v${BuildConfig.SDK_VERSION_NAME}",
    )
}

@Composable
private fun SettingsSeparator() {
    HorizontalDivider()
}

@OptIn(ExperimentalSharedTransitionApi::class)
@PreviewLightDark
@Composable
private fun Preview() {
    AppTheme {
        SettingsScreenInternal(
            state = SettingsUiState(customKeysEnabled = false),
            onGoToDetails = {}
        )
    }
}
