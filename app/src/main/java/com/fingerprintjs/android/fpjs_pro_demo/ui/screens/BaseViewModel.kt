package com.fingerprintjs.android.fpjs_pro_demo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.Syntax
import org.orbitmvi.orbit.viewmodel.container

abstract class BaseViewModel<UiState : Any, SideEffect : Any, UserAction : Any>(
    initialUiState: UiState
) : ContainerHost<UiState, SideEffect>, ViewModel() {

    private val userActions = Channel<UserAction>(Channel.BUFFERED)

    override val container = container<UiState, SideEffect>(
        initialState = initialUiState,
        onCreate = {
            intent {
                userActions.consumeEach { action ->
                    processUserAction(action)
                }
            }

            onCreate()
        }
    )

    protected abstract fun processUserAction(action: UserAction)

    protected fun onCreate() {
        // can be overloaded
    }

    fun act(action: UserAction) = intent {
        userActions.send(action)
    }

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
                ?: error("unexpected state: ${state.javaClass.name}")

        }
    }

    inline fun <reified T : UiState> reduce(
        noinline reducer: (T) -> UiState
    ) {
        intent {
            reduceState { state ->
                (state as? T)?.let(reducer) ?: error("unexpected state: ${state.javaClass.name}")
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

    fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    /**
     * Observe latest value from target Flow
     *
     * @param action Called on each item emitted by target Flow
     */
    protected fun <T> Flow<T>.observeLatest(
        action: suspend Syntax<UiState, SideEffect>.(T) -> Unit
    ) = intent {
        repeatOnSubscription {
            collectLatest { action(it) }
        }
    }
}

class NoSideEffect
