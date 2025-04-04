package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fingerprintjs.android.fpjs_pro_demo.constants.URLs
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.Drn
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.TapToBegin
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.subscreen.UiError
import com.fingerprintjs.android.fpjs_pro_demo.utils.IntentUtils
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun DrnScreen(modifier: Modifier) {
    val viewModel = injectedViewModel { drnViewModel }
    ViewState(modifier, viewModel)
}

@Composable
private fun ViewState(modifier: Modifier, viewModel: DrnViewModel) {
    val context = LocalContext.current
    when (val state = viewModel.collectAsState().value) {
        DrnUiState.Initial -> TapToBegin(
            modifier = modifier,
            onTapToBegin = { viewModel.act(DrnUserAction.OnTryAgainClicked) })

        is DrnUiState.Error -> UiError(
            modifier = modifier,
            error = state.error,
            onBtnClicked = { viewModel.act(DrnUserAction.OnTryAgainClicked) },
            onLinkClicked = {  IntentUtils.openUrl(context, URLs.support) })

        is DrnUiState.Main -> MainState(
            modifier = modifier,
            loading = state.loading,
            drn = state.drn,
        )
    }
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