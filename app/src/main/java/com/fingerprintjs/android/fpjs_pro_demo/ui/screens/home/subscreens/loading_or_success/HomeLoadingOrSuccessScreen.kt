package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.Shimmable
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.ShimmableState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.EventDetailsView
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.signup_prompt_view.SignupPromptView
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks.LoadingMocked
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks.SuccessMocked
import com.fingerprintjs.android.fpjs_pro_demo.utils.copyOnLongPress

@Composable
fun HomeLoadingOrSuccessScreen(
    modifier: Modifier,
    state: HomeScreenUiState.LoadingOrSuccess,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val title = when (state.isLoading) {
                true -> "Hang tight,"
                false -> "Your Device ID"
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = title,
                transitionSpec = { fadeIn().togetherWith(fadeOut()) },
                label = "Title animation",
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = it,
                    style = AppTheme.materialTheme.typography.headlineSmall,
                    color = AppTheme.materialTheme.colorScheme.onBackground,
                )
            }
            val expandTransition = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(),
            ) + fadeIn(
                animationSpec = tween(delayMillis = 100)
            )
            val shrinkTransition = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(delayMillis = 100)
            ) + fadeOut(
                animationSpec = tween()
            )
            AnimatedVisibility(
                visible = state.isLoading,
                enter = expandTransition,
                exit = shrinkTransition,
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "generating a unique Visitor ID for your device...",
                    style = AppTheme.materialTheme.typography.bodyLarge,
                    color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Visitor ID",
                style = AppTheme.materialTheme.typography.labelLarge,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Shimmable(
                state = ShimmableState(
                    isShimmed = state.isLoading,
                    data = state.visitorId,
                ),
            ) { state ->
                Text(
                    modifier= Modifier
                        .copyOnLongPress(
                            data = state.data,
                            enabled = !state.isShimmed,
                        ),
                    text = state.data,
                    maxLines = 2,
                    style = AppTheme.materialTheme.typography.headlineSmall,
                    color = AppTheme.materialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(
                visible = state.isSignupPromptShown,
                enter = expandTransition,
                exit = shrinkTransition,
            ) {
                Column {
                    SignupPromptView(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onHideClicked = state.onHideSignupPromptClicked,
                        onSignUpClicked = state.onSignupPromptClicked,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
            Text(
                text = "SDK Response",
                style = AppTheme.materialTheme.typography.labelLarge,
                color = AppTheme.materialTheme.colorScheme.onSurfaceVariant,
            )
        }

        EventDetailsView(
            modifier = Modifier.wrapContentSize(),
            prettifiedProperties = state.prettifiedProps,
            rawJson = state.rawJson,
            isLoading = state.isLoading,
        )
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Loading() {
    AppTheme {
        Surface {
            HomeLoadingOrSuccessScreen(
                modifier = Modifier.fillMaxSize(),
                state = HomeScreenUiState.LoadingOrSuccess.LoadingMocked,
            )
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun Success() {
    AppTheme {
        Surface {
            HomeLoadingOrSuccessScreen(
                modifier = Modifier.fillMaxSize(),
                state = HomeScreenUiState.LoadingOrSuccess.SuccessMocked,
            )
        }
    }
}
