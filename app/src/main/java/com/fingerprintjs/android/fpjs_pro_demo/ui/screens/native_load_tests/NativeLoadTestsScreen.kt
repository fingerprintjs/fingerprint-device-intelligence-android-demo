package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.native_load_tests

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.NativeLoadTestCase
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme

@Composable
fun NativeLoadTestsScreen(
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = injectedViewModel { nativeLoadTestsViewModel }
    val state by viewModel.state.collectAsState()

    NativeLoadTestsScreenInternal(
        state = state,
        onGoBack = onGoBack,
        onRunCase = viewModel::runCase,
        onClearLog = viewModel::clearLog,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NativeLoadTestsScreenInternal(
    state: NativeLoadTestsUiState,
    onGoBack: () -> Unit,
    onRunCase: (NativeLoadTestCase) -> Unit,
    onClearLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val busy = state.runningCase != null
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Native load tests") },
                navigationIcon = {
                    IconButton(onClick = onGoBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onClearLog, enabled = !busy) {
                        Text("Clear log")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Run one case at a time. Uses a fresh SDK client each run.",
                style = MaterialTheme.typography.bodyMedium,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
            )

            NativeLoadTestCase.entries.forEach { case ->
                val runningThis = state.runningCase == case
                Button(
                    onClick = { onRunCase(case) },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (runningThis) "Running…" else case.title)
                }
                Text(
                    text = case.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            state.lastSuccess?.let { ok ->
                Text(
                    text = if (ok) "Last run: PASS" else "Last run: FAIL",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (ok) {
                        AppTheme.materialTheme.colorScheme.primary
                    } else {
                        AppTheme.materialTheme.colorScheme.error
                    },
                )
            }

            Text(
                text = "Log",
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = state.log,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
            )

            OutlinedButton(
                onClick = onGoBack,
                enabled = !busy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Back to Settings")
            }
        }
    }
}
