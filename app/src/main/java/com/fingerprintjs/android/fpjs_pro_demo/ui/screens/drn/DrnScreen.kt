package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel

@Composable
fun DrnScreen(modifier: Modifier) {
    val viewModel = injectedViewModel { drnViewModel }
    val drn = viewModel.drn.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val state = if (drn.value.regionalActivity == null) {
            "Loading..."
        } else {
            "Loaded: ${drn.value}"
        }
        Text("state: $state")
    }
}
