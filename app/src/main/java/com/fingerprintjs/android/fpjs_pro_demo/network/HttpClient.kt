package com.fingerprintjs.android.fpjs_pro_demo.network

import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import com.fingerprintjs.android.fpjs_pro_demo.utils.executeAsync
import com.fingerprintjs.android.fpjs_pro_demo.utils.runCatchingCancellable
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.mapError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject


@AppScope
class HttpClient @Inject constructor(private val client: OkHttpClient) {

    sealed class Error <T: Throwable> (val cause: T) {
        class IO(error: IOException): Error<IOException>(error)
        class Unknown(error: Throwable): Error<Throwable>(error)

       companion object {
           fun from(t: Throwable): Error<*> = when (t) {
               is IOException -> IO(t)
               else -> Unknown(t)
           }
       }
    }

    data class Response(
        val body: String?,
        val code: Int,
    ) {
        val isSuccessful: Boolean
            get() = code in 200..299
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun request(
        url: String,
        headers: Map<String, String>,
    ): Result<Response, Error<*>> {
        val request: Request = Request.Builder()
            .url(url)
            .headers(headers.toHeaders())
            .get()
            .build()
        return runCatchingCancellable {
            client.newCall(request).executeAsync().use { response ->
                withContext(Dispatchers.IO) {
                    Response(
                        code = response.code,
                        body = response.body?.string(),
                    )
                }
            }
        }
            .mapError { Error.from(it) }
    }
}
