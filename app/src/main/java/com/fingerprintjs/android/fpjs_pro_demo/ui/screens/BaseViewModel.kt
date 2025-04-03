package com.fingerprintjs.android.fpjs_pro_demo.ui.screens

import androidx.lifecycle.ViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<UiState : Any, SideEffect : Any, UserAction : Any>(
    initialUiState: UiState
) : ContainerHost<UiState, SideEffect>, ViewModel() {
    override val container = container<UiState, SideEffect>(
        initialState = initialUiState,
        onCreate = {
            onCreate()
        }
    )

    protected fun onCreate() {}

    protected fun sendEffect(effect: SideEffect) {
        intent {
            postSideEffect(effect)
        }
    }

    inline fun <reified T : UiState> onState(
        crossinline callback: suspend (T) -> Unit
    ) {
        intent {
            (state as? T)?.let { callback(it) }
                ?: throw IllegalStateException("unexpected state: ${state.javaClass.name}")
        }
    }

    inline fun <reified T : UiState> reduce(
        noinline reducer: (T) -> UiState
    ) {
        intent {
            reduceState { state ->
                (state as? T)?.let(reducer)
                    ?: throw IllegalStateException("unexpected state: ${state.javaClass.name}")
            }
        }
    }

    fun reduce(state: UiState) {
        intent {
            reduceState { state }
        }
    }

    suspend fun Syntax<UiState, SideEffect>.reduceState(
        reducer: (UiState) -> UiState
    ) = reduce {
        reducer(state)
    }
}
