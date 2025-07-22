package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel

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
        "requestId": "1111111111111.AAAAAA",
        "visitorId": "rVC74CiaXVZGVC69OBsP",
        "confidenceScore": {
            "score": 1.0
        },
        "visitorFound": true,
        "ipAddress": "192.192.192.192",
        "ipLocation": {
            "accuracyRadius": 20,
            "latitude": 20.202,
            "longitude": 20.202,
            "postalCode": "123456",
            "timezone": "Europe / Berlin",
            "city": {
                "name": "Berlin"
            },
            "country": {
                "name": "Germany",
                "code": "DE"
            },
            "continent": {
                "name": "Europe",
                "code": "EU"
            },
            "subdivisions": [
                {
                    "name": "Berlin",
                    "isoCode": "DE-BE"
                }
            ]
        },
        "osName": "Android",
        "osVersion": "13",
        "firstSeenAt": {
            "global": "2024-01-16T01:01:01.587Z",
            "subscription": "2024-01-16T01:01:01.587Z"
        },
        "lastSeenAt": {
            "global": "2024-01-20T01:01:01.587Z",
            "subscription": "2024-01-20T01:01:01.587Z"
        }
    },
    "smartSignals": {
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