package com.fingerprintjs.android.fpjs_pro_demo.di.modules

import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
class AppModule {

    @Provides
    @AppScope
    @Named("networkTimeoutMillis")
    fun provideNetworkCallTimeoutMillis(): Int = 60_000

    @Provides
    @AppScope
    fun provideOkHttpClient(
        @Named("networkTimeoutMillis") networkTimeoutMillis: Int,
    ): OkHttpClient {
        val timeout = networkTimeoutMillis.toLong()
        val timeUnit = TimeUnit.MILLISECONDS
        return OkHttpClient.Builder()
            .callTimeout(timeout, timeUnit)
            .connectTimeout(timeout, timeUnit)
            .writeTimeout(timeout, timeUnit)
            .readTimeout(timeout, timeUnit)
            .build()
    }

    @Provides
    @AppScope
    fun provideApplicationCoroutineScope(): CoroutineScope {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
        return CoroutineScope(
            SupervisorJob() + Dispatchers.Default + exceptionHandler
        )
    }
}
