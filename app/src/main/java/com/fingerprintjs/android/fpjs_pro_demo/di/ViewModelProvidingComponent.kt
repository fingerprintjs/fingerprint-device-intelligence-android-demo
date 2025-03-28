package com.fingerprintjs.android.fpjs_pro_demo.di

import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn.viewmodel.DRNViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.details.SettingsDetailsViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.settings.main.SettingsViewModel

interface ViewModelProvidingComponent {
    val homeViewModel: HomeViewModel
    val drnViewModel: DRNViewModel
    val settingsDetailsViewModel: SettingsDetailsViewModel
    val settingsViewModel: SettingsViewModel
}
