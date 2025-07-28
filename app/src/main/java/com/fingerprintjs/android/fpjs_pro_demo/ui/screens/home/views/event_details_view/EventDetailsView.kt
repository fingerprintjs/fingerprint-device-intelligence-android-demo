package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeScreenUiState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.EventPrettifiedView
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.EventRawJsonView
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import com.fingerprintjs.android.fpjs_pro_demo.utils.ShowPreview
import kotlinx.coroutines.launch

private enum class EventDetailsViewTabs(
    val title: String,
) {
    Prettified(
        title = "Prettified",
    ),
    Raw(
        title = "Raw",
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventDetailsView(
    modifier: Modifier,
    prettifiedProperties: List<PrettifiedProperty>,
    rawJson: String?,
    isLoading: Boolean,
    isSmartSignalsLoading: Boolean,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { EventDetailsViewTabs.entries.size }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            pagerState.scrollToPage(EventDetailsViewTabs.Prettified.ordinal)
        }
    }

    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        TabRow(
            modifier = Modifier
                .fillMaxWidth(),
            contentColor = AppTheme.materialTheme.colorScheme.onSurface,
            selectedTabIndex = pagerState.currentPage,
        ) {
            EventDetailsViewTabs.entries.forEachIndexed { index, tab ->
                val isTabActive = index == pagerState.currentPage

                Tab(
                    text = {
                        Text(
                            color = when (isTabActive) {
                                true -> AppTheme.materialTheme.colorScheme.onBackground
                                false -> AppTheme.materialTheme.colorScheme.onSurfaceVariant.let {
                                    if (!isLoading) {
                                        it
                                    } else {
                                        it.copy(alpha = 0.5f)
                                    }
                                }
                            },
                            text = tab.title,
                            style = AppTheme.materialTheme.typography.titleSmall,
                        )
                    },
                    selected = pagerState.currentPage == tab.ordinal,
                    enabled = !isLoading,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            modifier = Modifier
                .wrapContentSize()
                .animateContentSize(),
            state = pagerState,
            verticalAlignment = Alignment.Top,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                EventDetailsViewTabs.Prettified.ordinal -> {
                    EventPrettifiedView(
                        modifier = Modifier.wrapContentSize(),
                        properties = prettifiedProperties,
                        isLoading = isLoading,
                        isSmartSignalsLoading = isSmartSignalsLoading
                    )
                }

                EventDetailsViewTabs.Raw.ordinal -> {
                    EventRawJsonView(
                        modifier = Modifier.wrapContentSize(),
                        code = rawJson.orEmpty()
                    )
                }
            }
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun EventDetailsViewPreview() {
    ShowPreview {
        EventDetailsView(
            modifier = Modifier.fillMaxSize(),
            rawJson = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.rawJson,
            prettifiedProperties = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.prettifiedProps,
            isLoading = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.isLoading,
            isSmartSignalsLoading = HomeScreenUiState.Content.LoadingOrSuccess.SuccessMocked.isSmartSignalsLoading
        )
    }
}
