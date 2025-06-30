package com.fingerprintjs.android.fpjs_pro_demo.di

import com.fingerprintjs.android.fpjs_pro_demo.App
import com.fingerprintjs.android.fpjs_pro_demo.MainActivity
import com.fingerprintjs.android.fpjs_pro_demo.di.components.common.CommonComponent
import com.fingerprintjs.android.fpjs_pro_demo.di.modules.AppBindingModule
import com.fingerprintjs.android.fpjs_pro_demo.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component


@AppScope
@Component(
    modules = [
        AppBindingModule::class,
        AppModule::class,
    ],
    dependencies = [
        CommonComponent::class
    ]
)
interface AppComponent: ViewModelProvidingComponent {

    fun inject(activity: MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        fun commonComponent(component: CommonComponent): Builder

        @BindsInstance
        fun app(application: App): Builder
    }
}
