package com.fingerprintjs.android.fpjs_pro_demo.di.components.common

import com.fingerprintjs.android.fpjs_pro_demo.domain.smart_signals.SmartSignalsBodyParser
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiStateCreator
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import dagger.Component
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * A non-context component needed not only for the app, but also for tests and previews.
 */
@Singleton
@Component(
    modules = [
        CommonModule::class,
    ]
)
interface CommonComponent {

    fun json(): Json
    fun stateMocks(): StateMocks
    fun homeScreenUiStateCreator(): HomeScreenUiStateCreator
    fun smartSignalsBodyParser(): SmartSignalsBodyParser
}
