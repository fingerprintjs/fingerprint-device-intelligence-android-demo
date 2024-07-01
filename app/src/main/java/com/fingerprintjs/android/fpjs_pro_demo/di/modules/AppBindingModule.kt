package com.fingerprintjs.android.fpjs_pro_demo.di.modules

import android.app.Application
import android.content.Context
import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.storage.BackingStorage
import com.fingerprintjs.android.fpjs_pro_demo.storage.impl.JsonSerializer
import com.fingerprintjs.android.fpjs_pro_demo.storage.Serializer
import com.fingerprintjs.android.fpjs_pro_demo.storage.impl.SharedPreferencesStorage
import dagger.Binds
import dagger.Module

@Module
interface AppBindingModule {

    @Binds
    fun bindAppToApplication(application: App): Application

    @Binds
    fun bindAppToContext(application: App): Context

    @Binds
    fun bindSerializer(serializer: JsonSerializer): Serializer

    @Binds
    fun bindBackingStorage(backingStorage: SharedPreferencesStorage): BackingStorage
}
