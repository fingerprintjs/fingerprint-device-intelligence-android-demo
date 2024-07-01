package com.fingerprintjs.android.fpjs_pro_demo.di.modules

import android.app.Application
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class AppModule {

    // todo: unused. remove it
    @Provides
    @AppScope
    fun provideFingerprintJS(context: Application): FingerprintJS {
        return FingerprintJSFactory(context).createInstance(
            Configuration(
                apiKey = Credentials.apiKey,
                endpointUrl = Credentials.endpointUrl,
                extendedResponseFormat = true,
            )
        )
    }

    @Provides
    @AppScope
    fun provideObjectMapper(): ObjectMapper {
        return ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }
}
