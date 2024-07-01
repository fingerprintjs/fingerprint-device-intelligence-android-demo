package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.outlined.Mail
import com.fasterxml.jackson.databind.ObjectMapper
import com.fingerprintjs.android.fpjs_pro.ConfidenceScore
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.fingerprintjs.android.fpjs_pro.IpLocation
import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.Timestamp
import com.fingerprintjs.android.fpjs_pro.TooManyRequest
import com.fingerprintjs.android.fpjs_pro.UnknownError
import com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys.CustomApiKeysState
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsDto
import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.toSmartSignals
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.create
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenuItem
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details.SettingsDetailsUiState

object StateMocks {
    val fingerprintJSResponse = FingerprintJSProResponse(
        requestId = "1111111111111.AAAAAA",
        visitorId = "rVC74CiaXVZGVC69OBsP",
        confidenceScore = ConfidenceScore(score = 1.0),
        visitorFound = true,
        ipAddress = "192.192.192.192",
        ipLocation = IpLocation(
            accuracyRadius = 20,
            latitude = 20.2020,
            longitude = 20.2020,
            postalCode = "123456",
            timezone = "Europe / Berlin",
            city = IpLocation.City(name = "Berlin"),
            country = IpLocation.Country(code = "DE", name = "Germany"),
            continent = IpLocation.Continent(code = "EU", name = "Europe"),
            subdivisions = listOf(IpLocation.Subdivisions(isoCode = "DE-BE", name = "Berlin"))
        ),
        osName = "Android",
        osVersion = "13",
        firstSeenAt = Timestamp(
            global = "2024-01-16T01:01:01.587Z",
            subscription = "2024-01-16T01:01:01.587Z",
        ),
        lastSeenAt = Timestamp(
            global = "2024-01-20T01:01:01.587Z",
            subscription = "2024-01-20T01:01:01.587Z",
        ),
        asJson = "{\"browserName\":\"Other\",\"browserVersion\":\"\",\"confidence\":{\"score\":1},\"device\":\"Pixel 4 XL\",\"firstSeenAt\":{\"global\":\"2024-01-16T01:01:01.587Z\",\"subscription\":\"2024-01-16T01:01:01.587Z\"},\"incognito\":false,\"ip\":\"192.192.192.192\",\"ipLocation\":{\"accuracyRadius\":20,\"city\":{\"name\":\"Berlin\"},\"continent\":{\"code\":\"EU\",\"name\":\"Europe\"},\"country\":{\"code\":\"DE\",\"name\":\"Germany\"},\"latitude\":20.2020,\"longitude\":20.2020,\"postalCode\":\"123456\",\"subdivisions\":[{\"isoCode\":\"DE-BE\",\"name\":\"Berlin\"}],\"timezone\":\"Europe\\/Berlin\"},\"lastSeenAt\":{\"global\":\"2024-01-20T01:01:01.587Z\",\"subscription\":\"2024-01-20T01:01:01.587Z\"},\"meta\":{\"version\":\"v1.1.2221+e341fd375\"},\"os\":\"Android\",\"osVersion\":\"13\",\"visitorFound\":true,\"visitorId\":\"rVC74CiaXVZGVC69OBsP\"}",
        errorMessage = null
    )

    val smartSignalsRawResponse = """
{
    "products": {
        "clonedApp": {
            "data": {
                "result": false
            }
        },
        "emulator": {
            "data": {
                "result": false
            }
        },
        "factoryReset": {
            "data": {
                "time": "2024-02-05T14:54:36Z",
                "timestamp": 1707144876
            }
        },
        "frida": {
            "data": {
                "result": false
            }
        },
        "highActivity": {
            "data": {
                "result": true,
                "dailyRequests": 125
            }
        },
        "locationSpoofing": {
            "data": {
                "result": false
            }
        },
        "rootApps": {
            "data": {
                "result": false
            }
        },
        "vpn": {
            "data": {
                "result": true,
                "originTimezone": "America/New_York",
                "originCountry": "DE",
                "methods": {
                    "timezoneMismatch": true,
                    "publicVPN": false,
                    "auxiliaryMobile": false,
                    "osMismatch": false
                }
            }
        }
    }
}
    """.trimIndent()
    val smartSignalsDto = ObjectMapper().readValue(smartSignalsRawResponse, SmartSignalsDto::class.java)
    @SuppressLint("VisibleForTests")
    val smartSignals = smartSignalsDto.toSmartSignals()

    val fingerprintJSNetworkError = NetworkError()
    val fingerprintJSTooManyRequestsError = TooManyRequest(
        requestId = "1111111111111.AAAAAA",
        errorDescription = "Some error description"
    )
    val fingerprintJSOtherError = UnknownError()

    val HomeScreenUiState.Companion.Mocked
        get() = HomeScreenUiState(
            reload = HomeScreenUiState.ReloadState(false, {}),
            appBar = HomeScreenUiState.AppBarState({}, {}, {}),
            mocking = null,
            content = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked,
        )

    val HomeScreenUiState.Content.LoadingOrSuccess.Companion.SuccessMocked
        get() = HomeScreenUiState.Content.LoadingOrSuccess.create(
            fingerprintJSProResponse = fingerprintJSResponse,
            smartSignals = smartSignals,
            isLoading = false,
        )

    val HomeScreenUiState.Content.LoadingOrSuccess.Companion.LoadingMocked
        get() = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.copy(
            isLoading = true,
        )

    val HomeScreenUiState.Content.TapToBegin.Companion.Mocked
        get() = HomeScreenUiState.Content.TapToBegin(onTap = {})

    val HomeScreenUiState.Content.Error.Companion.Mocked: HomeScreenUiState.Content.Error
        get() = HomeScreenUiState.Content.Error.Unknown({}, {})

    val appBarDropdownMenuItems: List<List<AppBarDropdownMenuItem>>
        get() = listOf(
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
