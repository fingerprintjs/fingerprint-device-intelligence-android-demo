package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error

import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.LinkableText
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks.Mocked

@Composable
fun HomeErrorScreen(
    modifier: Modifier,
    state: HomeErrorScreenState,
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
                painter = painterResource(state.image),
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
            LinkableText(
                text = state.description,
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
                Text("Try again")
            }
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Preview() {
    AppTheme {
        Surface {
            HomeErrorScreen(
                modifier = Modifier.fillMaxSize(),
                state = HomeErrorScreenState.Mocked,
            )
        }
    }
}
