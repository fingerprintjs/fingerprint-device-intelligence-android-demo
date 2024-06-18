package com.fingerprintjs.android.fpjs_pro_demo.di.modules

import android.app.Application
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro.FingerprintJS
import com.fingerprintjs.android.fpjs_pro.FingerprintJSFactory
import com.fingerprintjs.android.fpjs_pro_demo.constants.Credentials
import com.fingerprintjs.android.fpjs_pro_demo.di.AppScope
import com.fingerprintjs.android.fpjs_pro_demo.network.SmartSignalsApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create

@Module
class AppModule {

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
    fun provideRetrofit(objectMapper: ObjectMapper): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(
                JacksonConverterFactory.create(objectMapper)
            )
            .baseUrl("http://localhost/") // won't be used, will pass entire url as a param
            .build()
    }

    @AppScope
    @Provides
    fun provideSmartSignalsApi(retrofit: Retrofit): SmartSignalsApi {
        return retrofit.create()
    }
}
