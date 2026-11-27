package com.fingerprintjs.android.fpjs_pro_demo.viewmodel

import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage
import com.fingerprintjs.android.fpjs_pro_demo.utils.stateMocks
import junit.framework.TestCase
import org.junit.Test

class HomeScreenUiStateCreatorUnitTests {
    @Test
    fun givenFpjsResponseAndSmartSignal_whenCreateRawJson_thenExpectedJsonReturned() {
        // given
        val fpjsResponse = stateMocks.fingerprintJSResponse
        val smartSignals = stateMocks.smartSignals
        val creator = CommonComponentStorage.commonComponent.homeScreenUiStateCreator()

        // when
        val json = creator.createRawJson(fpjsResponse, smartSignals)

        // then
        TestCase.assertEquals(expectedJson, json)
    }
}

private val expectedJson = """
{
    "identification": {
        "eventId": "1111111111111.AAAAAA",
        "visitorId": "rVC74CiaXVZGVC69OBsP"
    },
    "smartSignals": {
        "activeCall": false,
        "clonedApp": false,
        "developerTools": false,
        "deviceDetails": {
            "device_manufacturer": "Samsung",
            "device_model": "SM-G991U",
            "os_version": "13.0.0"
        },
        "emulator": false,
        "factoryReset": {
            "time": "2024-02-05T14:54:36Z",
            "timestamp": 1707144876
        },
        "frida": false,
        "locationSpoofing": false,
        "highActivity": true,
        "rootApps": false,
        "vpn": {
            "vpn": true,
            "vpn_confidence": "high",
            "vpn_origin_timezone": "America/New_York",
            "vpn_origin_country": "DE",
            "vpn_methods": {
                "timezone_mismatch": true,
                "public_vpn": false,
                "auxiliary_mobile": false,
                "relay": false
            }
        }
    }
}
""".trimIndent()
