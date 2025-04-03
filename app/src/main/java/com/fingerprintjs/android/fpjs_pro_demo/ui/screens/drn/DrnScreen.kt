package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.Drn
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.TapToBegin
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.UiError
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun DrnScreen(modifier: Modifier) {
    val viewModel = injectedViewModel { drnViewModel }
    ViewState(modifier, viewModel)
}

@Composable
private fun ViewState(modifier: Modifier, viewModel: DrnViewModel) {
    // temp code - start
    var loadData by remember { mutableStateOf(false) }
    if (loadData) {
        LaunchedEffect(Unit) {
            viewModel.loadData()
        }
    }
    // temp code - end

    when (val state = viewModel.collectAsState().value) {
        DrnUiState.Initial -> InitialState(modifier) {
            loadData = true
        }

        is DrnUiState.Error -> ErrorState(
            modifier = modifier,
            error = state.error
        )

        is DrnUiState.Main -> MainState(
            modifier = modifier,
            loading = state.loading,
            drn = state.drn,
        )
    }
}

@Composable
private fun InitialState(modifier: Modifier, onBtnClicked: () -> Unit) {
    TapToBegin(modifier = modifier, onTapToBegin = onBtnClicked)
}

@Composable
private fun ErrorState(modifier: Modifier, error: UiError) {
    UiError(
        modifier = modifier,
        error = error,
        onBtnClick = {
            // TODO
        }, onLinkClick = {
            // TODO
        })
}

@Composable
private fun MainState(modifier: Modifier, loading: Boolean, drn: Drn?) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val state = if (loading) {
            "Loading..."
        } else {
            "Loaded: $drn"
        }
        Text("state: $state")
    }
}