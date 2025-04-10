package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.DrnProvider
import com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag.FlagSpriteManager
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.BaseViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.NoSideEffect
import com.github.michaelbull.result.fold
import javax.inject.Inject

class DrnViewModel @Inject constructor(
    val flagSpriteManager: FlagSpriteManager,
    private val drnProvider: DrnProvider,
) : BaseViewModel<DrnUiState, NoSideEffect, DrnUserAction>(DrnUiState.Initial) {

    override fun processUserAction(action: DrnUserAction) = when(action) {
        DrnUserAction.OnTapToBeginClicked -> launch { loadData() }
        DrnUserAction.OnTryAgainClicked -> launch { loadData() }
    }

    private suspend fun loadData() {
        reduce(DrnUiState.Main(loading = true, drn = null))

        drnProvider.getDrn().fold({ data ->
            reduce(DrnUiState.Main(loading = false, drn = data))
        }, { err ->
            reduce(DrnUiState.Error(error = err.mapToUi()))
        })
    }
}
