package com.fingerprintjs.android.fpjs_pro_demo

import android.app.Application
import com.fingerprintjs.android.fpjs_pro_demo.di.AppComponent
import com.fingerprintjs.android.fpjs_pro_demo.di.DaggerAppComponent
import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponentStorage

class App : Application() {

    @Volatile
    private var appComponentOrNull: AppComponent? = null

    val appComponent: AppComponent
        get() = appComponentOrNull ?: synchronized(this) {
            appComponentOrNull ?: createAppComponent().also { appComponentOrNull = it }
        }

    override fun onCreate() {
        super.onCreate()
        appComponent
    }

    private fun createAppComponent(): AppComponent =
        DaggerAppComponent
            .builder()
            .commonComponent(CommonComponentStorage.commonComponent)
            .app(this)
            .build()
}
