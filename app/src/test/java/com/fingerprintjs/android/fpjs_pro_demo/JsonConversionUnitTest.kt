package com.fingerprintjs.android.fpjs_pro_demo

import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import com.fingerprintjs.android.fpjs_pro_demo.utils.toPrettyJson
import junit.framework.TestCase
import org.junit.Test

class JsonConversionUnitTest {
    @Test
    fun fingerprintJsResponseConvertedProperly() {
        TestCase.assertEquals(expectedJson, StateMocks.fingerprintJSResponse.toPrettyJson())
    }
}

private val expectedJson = """
    {
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
    }
""".trimIndent()