package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.DrnProvider
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.BaseViewModel
import com.github.michaelbull.result.fold
import javax.inject.Inject

class DrnViewModel @Inject constructor(
    private val drnProvider: DrnProvider,
) : BaseViewModel<DrnUiState, DrnSideEffect, DrnUserAction>(DrnUiState.Initial) {

    suspend fun loadData() {
        reduce(DrnUiState.Main(loading = true, drn = null))

        drnProvider.getDrn().fold({ data ->
            reduce(
                DrnUiState.Main(loading = false, drn = data)
            )
        }, { err ->
            reduce(DrnUiState.Error(error = err.mapToUi()))
        })
    }
}
