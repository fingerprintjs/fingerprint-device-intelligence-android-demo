package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.subscreens.loading_or_success

import androidx.compose.runtime.Stable
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModelState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.EventDetailsViewState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.from

@Stable
data class HomeLoadingOrSuccessScreenState(
    val visitorId: String,
    val isLoading: Boolean,
    val eventDetailsViewState: EventDetailsViewState,
) {
    companion object
}

fun HomeLoadingOrSuccessScreenState.Companion.from(
    state: HomeViewModelState.LoadingOrSuccess,
): HomeLoadingOrSuccessScreenState {
    return HomeLoadingOrSuccessScreenState(
        visitorId = state.visitorId,
        isLoading = state.isLoading,
        eventDetailsViewState = EventDetailsViewState.from(state)
    )
}
