package com.fingerprintjs.android.fpjs_pro_demo.domain.custom_api_keys

import com.fingerprintjs.android.fpjs_pro.Configuration

data class CustomApiKeysState(
    val public: String,
    val secret: String,
    val region: Configuration.Region,
    val enabled: Boolean,
) {
    companion object {
        val Default: CustomApiKeysState = CustomApiKeysState(
            public = "",
            secret = "",
            region = Configuration.Region.US,
            enabled = false,
        )
    }
}
