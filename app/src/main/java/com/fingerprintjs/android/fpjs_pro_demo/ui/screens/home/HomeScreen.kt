@file:OptIn(ExperimentalMaterial3Api::class)

package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.di.injectedViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error.HomeErrorScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success.HomeLoadingOrSuccessScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.tap_to_begin.HomeTapToBeginScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenu
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenuItem
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.ClipboardUtils
import com.fingerprintjs.android.fpjs_pro_demo.utils.IntentUtils
import com.fingerprintjs.android.fpjs_pro_demo.utils.ShowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onGoToApiKeysSettings: () -> Unit,
) {
    val viewModel = injectedViewModel { homeViewModel }
    val state by viewModel.state.collectAsState(initial = null)
    state?.let {
        HomeScreenInternal(
            modifier = modifier,
            state = it,
        )
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.externalLinkToOpen
            .onEach { link ->
                IntentUtils.openUrl(context, link)
            }
            .launchIn(this)

        viewModel.textToCopy
            .onEach { text ->
                ClipboardUtils.copyToClipboardAndNotifyUser(context, text)
            }
            .launchIn(this)

        viewModel.goToApiKeySettings
            .onEach { onGoToApiKeysSettings() }
            .launchIn(this)
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenInternal(
    modifier: Modifier = Modifier,
    state: HomeScreenUiState,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(
            bottom = 0.dp,
        ),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    actionIconContentColor = AppTheme.materialTheme.colorScheme.primary,
                ),
                title = {},
                actions = {
                    var dropdownExpanded by remember { mutableStateOf(false) }
                    if (state.mocking != null) {
                        TooltipBox(
                            state = rememberTooltipState(),
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                Text(
                                    modifier = Modifier
                                        .padding(8.dp),
                                    text = "Toggle mocking",
                                    color = AppTheme.materialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        ) {
                            IconButton(onClick = state.mocking.onToggle) {
                                Icon(
                                    imageVector = Icons.Outlined.BugReport,
                                    contentDescription = "Toggle mocking",
                                    tint = when (state.mocking.enabled) {
                                        true -> MaterialTheme.colorScheme.primary
                                        false -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                    IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Open Navigation Drawer",
                        )
                        val docIcon = Icons.AutoMirrored.Filled.MenuBook
                        val mailIcon = Icons.Outlined.Mail
                        val openInNewIcon = Icons.AutoMirrored.Filled.OpenInNew
                        AppBarDropdownMenu(
                            expanded = dropdownExpanded,
                            sections = remember {
                                listOf(
                                    listOf(
                                        AppBarDropdownMenuItem(
                                            icon = docIcon,
                                            description = "Documentation",
                                            onClick = {
                                                state.appBar.onDocumentationClicked()
                                                dropdownExpanded = false
                                            },
                                        ),
                                        AppBarDropdownMenuItem(
                                            icon = mailIcon,
                                            description = "Support",
                                            onClick = {
                                                state.appBar.onSupportClicked()
                                                dropdownExpanded = false
                                            },
                                        ),
                                    ),
                                    listOf(
                                        AppBarDropdownMenuItem(
                                            icon = openInNewIcon,
                                            description = "Sign up",
                                            onClick = {
                                                state.appBar.onSignupClicked()
                                                dropdownExpanded = false
                                            },
                                        ),
                                    ),
                                )
                            },
                            onDismiss = { dropdownExpanded = false },
                        )
                    }
                },
            )
        },
    ) { paddingValues: PaddingValues ->
        val pullToRefreshEnabled by rememberUpdatedState(state.reload.reloadAllowed)
        var pullToRefreshRefreshing by remember { mutableStateOf(false) }
        val pullToRefreshState = rememberPullToRefreshState()

        val scope = rememberCoroutineScope()

        val transition = updateTransition(
            targetState = state.content,
            label = "Home screen state transition",
        )
        transition.Crossfade(
            contentKey = { it::class },
        ) { contentState ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = pullToRefreshRefreshing,
                        enabled = pullToRefreshEnabled,
                        state = pullToRefreshState,
                    ) {
                        scope.launch {
                            pullToRefreshRefreshing = true
                            state.reload.onReload()
                            delay(500)
                            pullToRefreshRefreshing = false
                        }
                    }
                    // this modifier is needed to ensure that scroll is always available,
                    // so that we can use pull to refresh from anywhere inside this Box
                    .scrollable(
                        state = rememberScrollableState { delta -> 0f },
                        orientation = Orientation.Vertical,
                    )
                    .padding(paddingValues),
            ) {
                when (contentState) {
                    is HomeScreenUiState.Content.TapToBegin -> {
                        HomeTapToBeginScreen(
                            modifier = Modifier.fillMaxSize(),
                            onTapToBegin = contentState.onTap,
                        )
                    }

                    is HomeScreenUiState.Content.Error -> {
                        HomeErrorScreen(
                            modifier = Modifier.fillMaxSize(),
                            state = contentState,
                        )
                    }

                    is HomeScreenUiState.Content.LoadingOrSuccess -> {
                        HomeLoadingOrSuccessScreen(
                            modifier = Modifier.fillMaxSize(),
                            state = contentState,
                        )
                    }
                }

                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = pullToRefreshRefreshing,
                    state = pullToRefreshState,
                    containerColor = AppTheme.materialTheme.colorScheme.surface,
                    color = AppTheme.materialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TapToBegin() {
    ShowPreview {
        HomeScreenInternal(state = HomeScreenUiState.Mocked.copy(content = HomeScreenUiState.Content.TapToBegin.Mocked))
    }
}

@PreviewLightDark
@Composable
private fun Success() {
    ShowPreview {
        HomeScreenInternal(
            state = HomeScreenUiState.Mocked.copy(content = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked)
        )
    }
}

@PreviewLightDark
@Composable
private fun Error() {
    ShowPreview {
        HomeScreenInternal(state = HomeScreenUiState.Mocked.copy(content = HomeScreenUiState.Content.Error.Mocked))
    }
}
