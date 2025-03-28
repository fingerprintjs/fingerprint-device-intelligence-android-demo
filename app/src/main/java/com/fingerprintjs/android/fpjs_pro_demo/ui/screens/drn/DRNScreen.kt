package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.drn

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel

@Composable
fun DRNScreen(modifier: Modifier) {
    val viewModel = injectedViewModel { drnViewModel }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text("vm: ${viewModel.state}")
    }
}
