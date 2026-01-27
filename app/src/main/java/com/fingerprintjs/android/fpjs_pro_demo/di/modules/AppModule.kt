package com.fingerprintjs.android.fpjs_pro_demo.di.modules

import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import dagger.Module
import dagger.Provides
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
}
