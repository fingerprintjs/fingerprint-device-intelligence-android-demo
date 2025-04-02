package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.ui.util.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.ui.util.ShowPreview

@Composable
fun HomeErrorScreen(
    modifier: Modifier,
    state: HomeScreenUiState.Content.Error,
) {
    Column(modifier = modifier) {
        Spacer(Modifier.weight(0.34f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .weight(0.66f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp),
                imageVector = state.image,
                contentDescription = "Error icon",
                colorFilter = ColorFilter.tint(color = AppTheme.materialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                style = AppTheme.materialTheme.typography.titleLarge,
                color = AppTheme.materialTheme.colorScheme.onBackground,
                text = state.title,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            val description = remember(state.description) {
                buildAnnotatedString { append(state.description) }
            }
            LinkableText(
                text = description,
                links = state.links,
                style = AppTheme.materialTheme.typography.bodyLarge,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = state.onButtonCLick,
            ) {
                Text(state.buttonTitle)
            }
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    ShowPreview {
        Surface {
            HomeErrorScreen(
                modifier = Modifier.fillMaxSize(),
                state = HomeScreenUiState.Content.Error.Mocked,
            )
        }
    }
}
