package com.fingerprintjs.android.fpjs_pro_demo.di

import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.di.modules.AppBindingModule
import com.fingerprintjs.android.fpjs_pro_demo.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component


@AppScope
@Component(
    modules = [
        AppBindingModule::class,
        AppModule::class,
    ]
)
interface AppComponent: ViewModelProvidingComponent {

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun app(application: App): Builder
    }
}
