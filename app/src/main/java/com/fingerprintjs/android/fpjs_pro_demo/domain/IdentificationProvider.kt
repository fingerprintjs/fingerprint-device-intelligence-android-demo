package com.fingerprintjs.android.fpjs_pro_demo.domain

import com.fingerprintjs.android.fpjs_pro.Error
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSProResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

typealias FingerprintJSProResult = Result<FingerprintJSProResponse, Error>

class IdentificationProvider @Inject constructor(private val fingerprintJs: FingerprintJS) {

    suspend fun getVisitorId(): FingerprintJSProResult {
        return withContext(Dispatchers.IO) {
            fingerprintJs.getVisitorId()
        }
    }

    private suspend fun FingerprintJS.getVisitorId(): FingerprintJSProResult {
        return suspendCancellableCoroutine { cancellableContinuation ->
            this.getVisitorId(
                listener = {
                    cancellableContinuation.resume(
                        Ok(it)
                    )
                },
                errorListener = {
                    cancellableContinuation.resume(
                        Err(it)
                    )
                },
            )
        }
    }
}
