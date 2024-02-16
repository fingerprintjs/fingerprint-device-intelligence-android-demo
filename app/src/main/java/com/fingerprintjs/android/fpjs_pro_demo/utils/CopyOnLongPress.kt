package com.fingerprintjs.android.fpjs_pro_demo.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.copyOnLongPress(
    data: String,
    enabled: Boolean = true,
): Modifier {
    val context = LocalContext.current
    return this.combinedClickable(
        enabled = enabled,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onLongClick = {
            ClipboardUtils.copyToClipboardAndNotifyUser(context, data)
        },
        onClick = {},
    )
}
