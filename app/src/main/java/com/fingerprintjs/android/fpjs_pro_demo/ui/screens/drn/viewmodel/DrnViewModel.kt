package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.Drn
import com.fingerprintjs.android.fpjs_pro_demo.domain.drn.DrnProvider
import com.github.michaelbull.result.fold
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DrnViewModel @Inject constructor(
    private val drnProvider: DrnProvider
) : ViewModel() {

    val drn = MutableStateFlow(Drn(
        regionalActivity = null,
        suspectScore = null,
        timestamps = null
    ))

    suspend fun loadData() {
        drnProvider.getDRN().fold({ data ->
            drn.value = data
        }, { err ->
            // TODO
            Log.d("DRN", "error: $err")
        })
    }
}
