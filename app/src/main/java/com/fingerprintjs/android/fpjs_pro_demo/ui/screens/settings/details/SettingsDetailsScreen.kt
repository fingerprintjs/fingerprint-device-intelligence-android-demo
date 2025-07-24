@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.IgnoringLocalMinimumInteractiveComponentEnforcement
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.ValuePickerTextField
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.animatedAppBar
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.IntentUtils
import com.fingerprintjs.android.fpjs_pro_demo.utils.ShowPreview
import com.fingerprintjs.android.fpjs_pro_demo.utils.description
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun SettingsDetailsScreen(
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    val viewModel = injectedViewModel { settingsDetailsViewModel }
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.backAction
            .onEach { onGoBack() }
            .launchIn(this)
    }

    state?.let {
        SettingsDetailsScreenInternal(
            state = it,
            modifier = modifier,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDetailsScreenInternal(
    state: SettingsDetailsUiState,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
) {
    val scope = rememberCoroutineScope()

    BackHandler {
        scope.launch {
            state.onLeave()
        }
    }

    state.validationPromptState?.let {
        AlertDialog(
            containerColor = AppTheme.materialTheme.colorScheme.surfaceContainerLow,
            onDismissRequest = it.onCancel,
            title = { Text(it.title) },
            text = { Text(it.description) },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                    ),
                    onClick = {
                        scope.launch { it.onContinue() }
                    }
                ) {
                    Text(it.continueButtonTitle)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { it.onCancel() }
                }) {
                    Text(it.cancelButtonTitle)
                }
            },
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                modifier = Modifier.animatedAppBar(sharedTransitionScope, animatedContentScope),
                colors = TopAppBarDefaults.topAppBarColors(
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = { scope.launch { state.onLeave() } }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                title = {
                    Text("API Keys")
                },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .fillMaxSize(),
        ) {
            val context = LocalContext.current
            LinkableText(
                text = buildAnnotatedString {
                    append("You can obtain API keys by logging in to fingerprint.com.")
                },
                links = listOf(
                    LinkableText.Link(
                        mask = "fingerprint.com",
                        handler = { IntentUtils.openUrl(context, URLs.main) }
                    )
                ),
                style = AppTheme.materialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Use your API keys",
                        style = AppTheme.materialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "When enabled, the app will use your API keys to make all the requests. " +
                            "These requests will count towards your monthly allowance.",
                        style = AppTheme.materialTheme.typography.bodyMedium,
                        color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                IgnoringLocalMinimumInteractiveComponentEnforcement {
                    Switch(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .padding(end = 8.dp),
                        checked = state.customApiKeysState.enabled,
                        onCheckedChange = state.onEnabledChanged,
                        colors = SwitchDefaults.colors(
                            uncheckedTrackColor = AppTheme.materialTheme.colorScheme.background,
                            uncheckedBorderColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                            uncheckedThumbColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "API keys",
                style = AppTheme.materialTheme.typography.bodyMedium,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.customApiKeysState.public,
                onValueChange = state.onPublicChanged,
                enabled = state.customApiKeysState.enabled,
                // todo: use ImeAction.Next if secret field is empty
                //  blocker: currently, after triggering ImeAction.Next,
                //  the screen does not scroll enough to fit the supporting text of the secret field.
                //  In contrast, when clicking the secret field directly, this is not an issue,
                //  which makes me hope it is a bug that will get fixed some day.
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                label = {
                    Text("Public Key")
                },
                supportingText = {
                    Text("A public key is required to get a visitor ID.")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = AppTheme.materialTheme.colorScheme.outline,
                ),
            )
            Spacer(modifier = Modifier.height(28.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.customApiKeysState.secret,
                onValueChange = state.onSecretChanged,
                enabled = state.customApiKeysState.enabled,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                label = {
                    Text("Secret Key")
                },
                supportingText = {
                    Text("A secret key is required to fetch Smart Signals.")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedLabelColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                    focusedBorderColor = AppTheme.materialTheme.colorScheme.outline,
                ),
            )
            Spacer(modifier = Modifier.height(28.dp))
            ValuePickerTextField(
                modifier = Modifier.fillMaxWidth(),
                enabled = state.customApiKeysState.enabled,
                label = "Server Region",
                supportingText = "Specify the server region of your Fingerprint app.",
                values = Configuration.Region.entries,
                currentValue = state.customApiKeysState.region,
                valueDescription = { description },
                onValueChanged = state.onRegionChanged,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    ShowPreview {
        SettingsDetailsScreenInternal(
            state = settingsDetailsUiState,
        )
    }
}

@PreviewLightDark
@Composable
private fun PreviewDisabled() {
    ShowPreview {
        SettingsDetailsScreenInternal(
            state = settingsDetailsUiStateDisabled,
        )
    }
}

@Preview
@Composable
private fun PreviewWithDialog() {
    ShowPreview {
        SettingsDetailsScreenInternal(state = settingsDetailsUiStateWithPrompt)
    }
}
