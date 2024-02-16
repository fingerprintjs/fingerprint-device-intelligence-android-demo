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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fingerprintjs.android.fpjs_pro_demo.R
import com.fingerprintjs.android.fpjs_pro_demo.ui.kit.rememberPullToRefreshStateCustom
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error.HomeErrorScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error.HomeErrorScreenState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.error.from
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success.HomeLoadingOrSuccessScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success.HomeLoadingOrSuccessScreenState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success.from
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.tap_to_begin.HomeTapToBeginScreen
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModel
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModelState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.reloadAllowed
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenu
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.links_dropdown_menu.AppBarDropdownMenuItem
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.IntentUtils
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks.SuccessMocked
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeScreen() {
    val appContext = LocalContext.current.applicationContext!!
    val viewModel: HomeViewModel = viewModel {
        HomeViewModel(appContext)
    }
    val state by viewModel.state.collectAsState()
    val mockingState by viewModel.mockingState.collectAsState()
    val onStartFingerprint by rememberUpdatedState(viewModel::onStartFingerprint)
    val onReloadFingerprint by rememberUpdatedState(viewModel::onReloadFingerprint)
    val onSupportClicked by rememberUpdatedState(viewModel::onSupportClicked)
    val onDocumentationClicked by rememberUpdatedState(viewModel::onDocumentationClicked)
    val onToggleMocking by rememberUpdatedState(viewModel::onToggleMocking)
    HomeScreenInternal(
        state = state,
        onStartFingerprint = onStartFingerprint,
        onReloadFingerprint = onReloadFingerprint,
        onSupportClicked = onSupportClicked,
        onDocumentationClicked = onDocumentationClicked,
        isMockEnabled = mockingState,
        onToggleMocking = onToggleMocking,
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.externalLinkToOpen
            .onEach { link ->
                IntentUtils.openUrl(context, link)
            }
            .launchIn(this)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreenInternal(
    state: HomeViewModelState,
    isMockEnabled: Boolean? = false,
    onStartFingerprint: () -> Unit = {},
    onReloadFingerprint: () -> Unit = {},
    onSupportClicked: () -> Unit = {},
    onDocumentationClicked: () -> Unit = {},
    onToggleMocking: () -> Unit = {},
) {
    Scaffold(
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
                    if (isMockEnabled != null) {
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
                            IconButton(onClick = onToggleMocking) {
                                Icon(
                                    imageVector = Icons.Filled.Build,
                                    contentDescription = "Toggle mocking",
                                    tint = when (isMockEnabled) {
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
                        AppBarDropdownMenu(
                            expanded = dropdownExpanded,
                            items = remember {
                                listOf(
                                    AppBarDropdownMenuItem(
                                        icon = R.drawable.ic_book_filled_left_page,
                                        description = "Documentation",
                                        onClick = {
                                            onDocumentationClicked()
                                            dropdownExpanded = false
                                        },
                                    ),
                                    AppBarDropdownMenuItem(
                                        icon = R.drawable.ic_mail,
                                        description = "Support",
                                        onClick = {
                                            onSupportClicked()
                                            dropdownExpanded = false
                                        },
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
        val pullToRefreshEnabled by rememberUpdatedState(state.reloadAllowed)

        val pullToRefreshState = rememberPullToRefreshStateCustom(
            enabled = { pullToRefreshEnabled }
        )

        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                onReloadFingerprint()
                delay(500)
                pullToRefreshState.endRefreshAnimated()
            }
        }

        val transition = updateTransition(
            targetState = state,
            label = "Home screen state transition",
        )
        transition.Crossfade(
            contentKey = { it::class },
        ) { state ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
                    // this modifier is needed to ensure that scroll is always available,
                    // so that we can use pull to refresh from anywhere inside this Box
                    .scrollable(
                        state = rememberScrollableState { delta -> 0f },
                        orientation = Orientation.Vertical,
                    )
                    .padding(paddingValues),
            ) {
                when (state) {
                    is HomeViewModelState.TapToBegin -> {
                        HomeTapToBeginScreen(
                            modifier = Modifier.fillMaxSize(),
                            onTapToBegin = onStartFingerprint,
                        )
                    }

                    is HomeViewModelState.Error -> {
                        HomeErrorScreen(
                            modifier = Modifier.fillMaxSize(),
                            state = HomeErrorScreenState.from(
                                state = state,
                                onSupport = onSupportClicked,
                                onReload = onReloadFingerprint,
                            ),
                        )
                    }

                    is HomeViewModelState.LoadingOrSuccess -> {
                        HomeLoadingOrSuccessScreen(
                            modifier = Modifier.fillMaxSize(),
                            state = HomeLoadingOrSuccessScreenState.from(state),
                        )
                    }
                }

                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    containerColor = AppTheme.materialTheme.colorScheme.surface,
                    contentColor = AppTheme.materialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TapToBegin() {
    AppTheme {
        HomeScreenInternal(state = HomeViewModelState.TapToBegin)
    }
}

@PreviewLightDark
@Composable
private fun Success() {
    AppTheme {
        HomeScreenInternal(state = HomeViewModelState.SuccessMocked)
    }
}

@PreviewLightDark
@Composable
private fun Error() {
    AppTheme {
        HomeScreenInternal(state = HomeViewModelState.Error.TooManyRequests)
    }
}
