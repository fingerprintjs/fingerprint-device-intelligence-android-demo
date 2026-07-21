package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.fingerprintjs.android.fpjs_pro.Error
import com.fingerprintjs.android.fpjs_pro.FingerprintResponse
import com.github.michaelbull.result.Result

typealias FingerprintJSProResult = Result<FingerprintResponse, Error>
