package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.native_load_tests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.NativeLoadTestCase
import com.fingerprintjs.android.fpjs_pro_demo.domain.identification.NativeLoadTestRunner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NativeLoadTestsUiState(
    val runningCase: NativeLoadTestCase? = null,
    val log: String = "Pick a case below. Run them one at a time.\n",
    val lastSuccess: Boolean? = null,
)

class NativeLoadTestsViewModel @Inject constructor(
    private val runner: NativeLoadTestRunner,
) : ViewModel() {

    private val _state = MutableStateFlow(NativeLoadTestsUiState())
    val state: StateFlow<NativeLoadTestsUiState> = _state.asStateFlow()

    fun runCase(case: NativeLoadTestCase) {
        if (_state.value.runningCase != null) return
        viewModelScope.launch {
            _state.update {
                it.copy(
                    runningCase = case,
                    lastSuccess = null,
                    log = it.log + "\n———\nRunning ${case.title}…\n",
                )
            }
            val result = runner.run(case)
            _state.update {
                it.copy(
                    runningCase = null,
                    lastSuccess = result.success,
                    log = it.log + result.log + "\nResult: ${if (result.success) "PASS" else "FAIL"}\n",
                )
            }
        }
    }

    fun clearLog() {
        _state.update {
            it.copy(log = "Log cleared.\n", lastSuccess = null)
        }
    }
}
