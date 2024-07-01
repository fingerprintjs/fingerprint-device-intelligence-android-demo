package com.fingerprintjs.android.fpjs_pro_demo.ui.kit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro.Configuration
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.description
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// todo (minor): remove ability to select text
@Composable
fun <T> ValuePickerTextField(
    label: String,
    supportingText: String,
    values: List<T>,
    currentValue: T,
    valueDescription: T.() -> String,
    onValueChanged: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val scope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            interactionSource
                .interactions
                .onEach {
                    if (it is PressInteraction.Press) {
                        dropdownExpanded = true
                    }
                }
                .launchIn(scope)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = currentValue.valueDescription(),
            readOnly = true,
            onValueChange = {},
            enabled = enabled,
            label = { Text(label) },
            supportingText = { Text(supportingText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                focusedBorderColor = AppTheme.materialTheme.colorScheme.outline,
                focusedTextColor = AppTheme.materialTheme.colorScheme.onSurface,
                unfocusedTextColor = AppTheme.materialTheme.colorScheme.onSurface,
            ),
            interactionSource = interactionSource,
        )
        DropdownMenu(
            containerColor = AppTheme.materialTheme.colorScheme.surfaceContainerLow,
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false },
        ) {
            values.forEach { value ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = value.valueDescription(),
                            style = AppTheme.materialTheme.typography.bodyLarge,
                            color = AppTheme.materialTheme.colorScheme.onSurface,
                        )
                    },
                    onClick = {
                        onValueChanged.invoke(value)
                        dropdownExpanded = false
                    },
                )
            }
        }

    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        ValuePickerTextField(
            modifier = Modifier.width(300.dp),
            label = "Server Region",
            supportingText = "Specify the server region of your Fingerprint app.",
            values = Configuration.Region.entries,
            currentValue = Configuration.Region.US,
            valueDescription = { description },
            onValueChanged = {}
        )
    }
}
