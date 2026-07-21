package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.Mail
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintResponse
import com.fingerprintjs.android.fpjs_pro.UnknownError
import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysState
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsBodyParser
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiStateCreator
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenuItem
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details.SettingsDetailsUiState
import com.github.michaelbull.result.get
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StateMocks @Inject constructor(
    private val homeScreenUiStateCreator: HomeScreenUiStateCreator,
    private val smartSignalsBodyParser: SmartSignalsBodyParser,
) {
    @Suppress("MaximumLineLength")
    val fingerprintJSResponse = FingerprintResponse(
        eventId = "1111111111111.AAAAAA",
        visitorId = "rVC74CiaXVZGVC69OBsP",
        asJson = "{\"event_id\":\"1111111111111.AAAAAA\",\"visitor_id\":\"rVC74CiaXVZGVC69OBsP\",\"suspect_score\":0}",
        errorMessage = null
    )

    val smartSignalsRawResponse = """
{
    "cloned_app": false,
    "emulator": false,
    "factory_reset_timestamp": 1707144876,
    "frida": false,
    "high_activity_device": true,
    "location_spoofing": false,
    "root_apps": false,
    "vpn": true,
    "vpn_confidence": "high",
    "vpn_origin_timezone": "America/New_York",
    "vpn_origin_country": "DE",
    "vpn_methods": {
        "timezone_mismatch": true,
        "public_vpn": false,
        "auxiliary_mobile": false,
        "relay": false
    },
    "developer_tools": false
}
    """.trimIndent()

    @SuppressLint("VisibleForTests")
    val smartSignals = smartSignalsBodyParser.parseSmartSignals(smartSignalsRawResponse).get()!!

    val fingerprintJSOtherError = UnknownError()

    val HomeScreenUiState.Companion.Mocked by lazy {
        HomeScreenUiState(
            reload = HomeScreenUiState.ReloadState(false, {}),
            appBar = HomeScreenUiState.AppBarState({}, {}, {}),
            mocking = null,
            content = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked,
        )
    }

    @Suppress("VariableNaming")
    val HomeScreenUiState.Content.LoadingOrSuccess.Companion.SuccessMocked by lazy {
        with(homeScreenUiStateCreator) {
            HomeScreenUiState.Content.LoadingOrSuccess.create(
                fingerprintJSProResponse = fingerprintJSResponse,
                smartSignals = smartSignals,
                isLoading = false,
                isSmartSignalsLoading = false,
                isAnyLocationPermissionGranted = false
            )
        }
    }

    val HomeScreenUiState.Content.LoadingOrSuccess.Companion.LoadingMocked by lazy {
        HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.copy(
            isLoading = true,
        )
    }

    val HomeScreenUiState.Content.TapToBegin.Companion.Mocked by lazy {
        HomeScreenUiState.Content.TapToBegin(onTap = {})
    }

    val HomeScreenUiState.Content.Error.Companion.Mocked: HomeScreenUiState.Content.Error by lazy {
        HomeScreenUiState.Content.Error.Unknown({}, {})
    }

    val appBarDropdownMenuItems: List<List<AppBarDropdownMenuItem>> = listOf(
        listOf(
            AppBarDropdownMenuItem(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                description = "Documentation",
                onClick = {},
            ),
            AppBarDropdownMenuItem(
                icon = Icons.Outlined.Mail,
                description = "Support",
                onClick = {},
            ),
        ),
        listOf(
            AppBarDropdownMenuItem(
                icon = Icons.AutoMirrored.Filled.OpenInNew,
                description = "Sign up",
                onClick = {},
            ),
        )
    )

    val customApiKeysState = CustomApiKeysState(
        public = "lkasdfj342508dgF48gf",
        secret = "Ufsdlf43845aFhdsfFuw",
        region = Configuration.Region.US,
        enabled = true,
    )

    val settingsDetailsUiState = SettingsDetailsUiState(
        customApiKeysState = customApiKeysState,
        onPublicChanged = {},
        onSecretChanged = {},
        onRegionChanged = {},
        onEnabledChanged = {},
        onLeave = {},
        null,
    )

    val settingsDetailsUiStateWithPrompt = settingsDetailsUiState.copy(
        validationPromptState = SettingsDetailsUiState.ValidationPromptState.InvalidKeysState(
            onCancel = {},
            onContinue = {},
        )
    )

    val settingsDetailsUiStateDisabled = settingsDetailsUiState.copy(
        customApiKeysState = settingsDetailsUiState.customApiKeysState.copy(enabled = false),
    )
}

val stateMocks: StateMocks
    get() = CommonComponentStorage.commonComponent.stateMocks()
