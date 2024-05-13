package com.fingerprintjs.android.fpjs_pro_demo

import android.app.Application
import com.fingerprintjs.android.fpjs_pro_demo.di.AppComponent
import com.fingerprintjs.android.fpjs_pro_demo.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .app(this)
            .build()
    }
}
