package com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view

import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.viewmodel.HomeViewModelState
import com.fingerprintjs.android.fpjs_pro_demo.ui.screens.home.views.event_details_view.tabs.PrettifiedProperty
import kotlin.math.round

data class EventDetailsViewState(
    val prettifiedProperties: List<PrettifiedProperty>,
    val rawJson: String?,
    val isLoading: Boolean,
) {

    companion object
}

fun EventDetailsViewState.Companion.from(state: HomeViewModelState.LoadingOrSuccess): EventDetailsViewState {
    val prettifiedProperties = listOf(
        PrettifiedProperty(
            name = "Request ID",
            value = state.requestId
        ),
        PrettifiedProperty(
            name = "Visitor ID",
            value = state.visitorId
        ),
        PrettifiedProperty(
            name = "Visitor Found",
            value = if (state.visitorFound) "Yes" else "No"
        ),
        PrettifiedProperty(
            name = "Confidence",
            value = "${round(state.confidence * 100).toInt()}%"
        ),
        PrettifiedProperty(
            name = "IP Address",
            value = state.ipAddress
        ),
        PrettifiedProperty(
            name = "IP Location",
            value = run {
                val country = state.ipCountry
                val city = state.ipCity
                when {
                    state.ipCountry != null && city != null -> "$city, $country"
                    country != null -> "$country"
                    else -> null
                }
            },
        ),
        PrettifiedProperty(
            name = "First Seen At",
            value = state.firstSeenAt
        ),
        PrettifiedProperty(
            name = "Last Seen At",
            value = state.lastSeenAt
        ),
    )
    return EventDetailsViewState(
        prettifiedProperties = prettifiedProperties,
        rawJson = state.rawJson,
        isLoading = state.isLoading,
    )
}
