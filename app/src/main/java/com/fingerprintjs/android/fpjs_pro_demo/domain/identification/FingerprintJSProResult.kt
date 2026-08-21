package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprint.android.Error
import com.fingerprint.android.FingerprintResponse
import com.github.michaelbull.result.Result

typealias FingerprintJSProResult = Result<FingerprintResponse, Error>
