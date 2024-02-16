package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error.HomeErrorScreenState
import com.fingerprintjs.android.fpjs_pro.ConfidenceScore
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.fingerprintjs.android.fpjs_pro.IpLocation
import com.fingerprintjs.android.fpjs_pro.NetworkError
import com.fingerprintjs.android.fpjs_pro.Timestamp
import com.fingerprintjs.android.fpjs_pro.TooManyRequest
import com.fingerprintjs.android.fpjs_pro.UnknownError
import com.fingerprintjs.android.fpjs_pro_demo.R
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModelState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenuItem

object StateMocks {
    val HomeViewModelState.Companion.SuccessMocked: HomeViewModelState.LoadingOrSuccess
        get() = HomeViewModelState.LoadingOrSuccess(
            requestId = "1694931396079.ES1eR5",
            visitorId = "rVC74CiaXVZGVC69OBsP",
            visitorFound = false,
            confidence = 0.9,
            ipAddress = "127.0.0.1",
            ipCity = "Johannesburg",
            ipCountry = "South Africa",
            firstSeenAt = "2019-10-12T07:20:50.52Z",
            lastSeenAt = "2019-10-12T07:20:50.52Z",
            rawJson = successRawJsonExample1,
            isLoading = false,
        )

    val HomeViewModelState.Companion.LoadingMocked: HomeViewModelState.LoadingOrSuccess
        get() = HomeViewModelState.SuccessMocked.copy(
            isLoading = true,
        )

    val successRawJsonExample1 = """
{
    "requestId": "Px6VxbRC6WBkA39yeNH3",
    "tag": {
        "requestType": "signup",
        "yourCustomId": 45321
    },
    "linkedId": "any-string",
    "visitorId": "3HNey93AkBW6CRbxV6xP",
    "visitorFound": true,
    "timestamp": 1554910997788,
    "time": "2019-10-12T07:20:50.52Z",
    "incognito": false,
    "url": "https://banking.example.com/signup",
    "clientReferrer": "https://google.com?search=banking+services",
    "ip": "216.3.128.12",
    "ipLocation": {
        "accuracyRadius": 1,
        "city": {
            "name": "Bolingbrook"
        },
        "continent": {
            "code": "NA",
            "name": "NorthAmerica"
        },
        "country": {
            "code": "US",
            "name": "UnitedStates"
        },
        "latitude": 41.12933,
        "longitude": -88.9954,
        "postalCode": "60547",
        "subdivisions": [{
            "isoCode": "IL",
            "name": "Illinois"
        }],
        "timezone": "America/Chicago"
    },
    "browserDetails": {
        "browserName": "Chrome",
        "browserFullVersion": "73.0.3683.86",
        "browserMajorVersion": "73",
        "os": "MacOSX",
        "osVersion": "10.14.3",
        "device": "Other",
        "userAgent": "(Macintosh;IntelMacOSX10_14_3)Chrome/73.0.3683.86"
    },
    "confidence": {
        "score": 0.97
    },
    "firstSeenAt": {
        "global": "2022-03-16T11:26:45.362Z",
        "subscription": "2022-03-16T11:31:01.101Z"
    },
    "lastSeenAt": {
        "global": "2022-03-16T11:28:34.023Z",
        "subscription": null
    },
    "bot": {
        "result": "notDetected"
    },
    "userAgent": "(Macintosh;IntelMacOSX10_14_3)Chrome/73.0.3683.86",
    "rootApps": {
        "result": false
    },
    "emulator": {
        "result": false
    },
    "ipBlocklist": {
        "result": false,
        "details": {
            "emailSpam": false,
            "attackSource": false
        }
    },
    "tor": {
        "result": false
    },
    "vpn": {
        "result": false,
        "methods": {
            "timezoneMismatch": false,
            "publicVPN": false
        }
    },
    "proxy": {
        "result": false
    },
    "tampering": {
        "result": false,
        "anomalyScore": 0
    },
    "clonedApp": {
        "result": false
    },
    "factoryReset": {
        "time": "1970-01-01T00:00:00Z",
        "timestamp": 0
    },
    "jailbroken": {
        "result": false
    },
    "frida": {
        "result": false
    },
    "privacySettings": {
        "result": false
    },
    "virtualMachine": {
        "result": false
    },
    "rawDeviceAttributes": {}
}
            """.trimIndent()

    val prettifiedProperties = listOf(
        PrettifiedProperty(
            name = "Request ID",
            value = HomeViewModelState.SuccessMocked.requestId,
        ),
        PrettifiedProperty(
            name = "Visitor ID",
            value = HomeViewModelState.SuccessMocked.visitorId,
        ),
        PrettifiedProperty(
            name = "Confidence",
            value = HomeViewModelState.SuccessMocked.confidence.toString(),
        ),
        PrettifiedProperty(
            name = "IP Address",
            value = HomeViewModelState.SuccessMocked.ipAddress,
        ),
        PrettifiedProperty(
            name = "IP Location",
            value = HomeViewModelState.SuccessMocked.ipCountry,
        ),
        PrettifiedProperty(
            name = "Previously Seen At",
            value = HomeViewModelState.SuccessMocked.lastSeenAt,
        ),
        PrettifiedProperty(
            name = "First Seen At",
            value = HomeViewModelState.SuccessMocked.firstSeenAt,
        ),
    )

    val appBarDropdownMenuItems = listOf(
        AppBarDropdownMenuItem(
            icon = R.drawable.ic_book_filled_left_page,
            description = "Documentation",
            onClick = {},
        ),
        AppBarDropdownMenuItem(
            icon = R.drawable.ic_mail,
            description = "Support",
            onClick = {},
        ),
        AppBarDropdownMenuItem(
            icon = R.drawable.ic_book_filled_left_page,
            description = "Another item 1",
            onClick = {},
        ),
    )

    val HomeErrorScreenState.Companion.Mocked: HomeErrorScreenState
        get() = HomeErrorScreenState(
            image = R.drawable.ic_exclamation_in_circle,
            title = "An unexpected error occurred..",
            description = "Please contact support if this issue persists.",
            links = listOf(
                LinkableText.Link(
                    mask = "contact support",
                    handler = {},
                )
            ),
            onButtonCLick = {},
        )

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

    val fingerprintJSNetworkError = NetworkError()
    val fingerprintJSTooManyRequestsError = TooManyRequest(
        requestId = "1111111111111.AAAAAA",
        errorDescription = "Some error description"
    )
    val fingerprintJSOtherError = UnknownError()
}
