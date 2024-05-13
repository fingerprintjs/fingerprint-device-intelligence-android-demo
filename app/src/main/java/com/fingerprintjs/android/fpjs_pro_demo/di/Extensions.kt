package com.fingerprintjs.android.fpjs_pro_demo.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fingerprintjs.android.fpjs_pro_demo.App

@Composable
fun getAppComponent(): AppComponent {
    return (LocalContext.current.applicationContext as App).appComponent
}

fun Context.getAppComponent(): AppComponent {
    return (this.applicationContext as App).appComponent
}

@Composable
inline fun <reified T : ViewModel> injectedViewModel(
    key: String? = null,
    crossinline viewModelInstanceCreator: ViewModelProvidingComponent.() -> T
): T {
    val appComponent = getAppComponent()
    return androidx.lifecycle.viewmodel.compose.viewModel(
        modelClass = T::class.java,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return appComponent.viewModelInstanceCreator() as T
            }
        }
    )
}
