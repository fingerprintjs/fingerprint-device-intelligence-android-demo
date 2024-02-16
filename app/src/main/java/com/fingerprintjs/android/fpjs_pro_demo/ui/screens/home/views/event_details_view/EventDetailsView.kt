package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view

import com.fingerprintjs.android.fpjs_pro_demo.utils.PreviewMultipleConfigurations
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.fingerprintjs.android.fpjs_pro_demo.utils.StateMocks
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.EventPrettifiedView
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.EventRawJsonView
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme
import kotlinx.coroutines.launch

private enum class EventDetailsViewTabs(
    val title: String,
) {
    Prettified(
        title = "Prettified",
    ),
    Raw(
        title = "Raw",
    );
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventDetailsView(
    modifier: Modifier,
    state: EventDetailsViewState
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { EventDetailsViewTabs.entries.size }

    LaunchedEffect(state) {
        if (state.isLoading) {
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
                                    if (!state.isLoading) it
                                    else it.copy(alpha = 0.5f)
                                }
                            },
                            text = tab.title,
                            style = AppTheme.materialTheme.typography.titleSmall,
                        )
                    },
                    selected = pagerState.currentPage == tab.ordinal,
                    enabled = !state.isLoading,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(tab.ordinal)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false,
        ) { page ->
            when (page) {
                EventDetailsViewTabs.Prettified.ordinal -> {
                    EventPrettifiedView(
                        modifier = Modifier.fillMaxSize(),
                        properties = state.prettifiedProperties,
                        isLoading = state.isLoading,
                    )
                }

                EventDetailsViewTabs.Raw.ordinal -> {
                    EventRawJsonView(
                        modifier = Modifier.fillMaxSize(),
                        code = state.rawJson.orEmpty()
                    )
                }
            }
        }
    }
}

@PreviewMultipleConfigurations
@Composable
private fun EventDetailsViewPreview() {
    AppTheme {
        EventDetailsView(
            modifier = Modifier.fillMaxSize(),
            state = EventDetailsViewState(
                prettifiedProperties = StateMocks.prettifiedProperties,
                rawJson = null,
                isLoading = false,
            ),
        )
    }
}
