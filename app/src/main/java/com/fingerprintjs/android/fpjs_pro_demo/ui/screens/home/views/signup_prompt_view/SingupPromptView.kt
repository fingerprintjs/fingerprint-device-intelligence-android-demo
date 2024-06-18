package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.signup_prompt_view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme

@Composable
fun SignupPromptView(
    modifier: Modifier = Modifier,
    onHideClicked: () -> Unit,
    onSignUpClicked: () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = AppTheme.materialTheme.shapes.medium,
        color = AppTheme.materialTheme.colorScheme.surfaceContainerLow,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                Text(
                    style = AppTheme.materialTheme.typography.titleLarge,
                    text = "Impressed with Fingerprint?"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    style = AppTheme.materialTheme.typography.bodyMedium,
                    text = "Try free for 14 days, credit card not needed.",
                    color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.materialTheme.colorScheme.surfaceContainerLow
                        ),
                        onClick = onHideClicked,
                    ) {
                        Text(
                            text = "Hide",
                            color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.materialTheme.colorScheme.inverseSurface
                        ),
                        onClick = onSignUpClicked,
                    ) {
                        Text(
                            text = "Sign Up",
                            color = AppTheme.materialTheme.colorScheme.inverseOnSurface,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppTheme {
        SignupPromptView(
            onHideClicked = {},
            onSignUpClicked = {},
        )
    }
}
