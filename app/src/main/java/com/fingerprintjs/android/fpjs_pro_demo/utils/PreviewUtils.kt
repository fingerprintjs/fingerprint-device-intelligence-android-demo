package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.fingerprintjs.android.fpjs_pro_demo.ui.theme.AppTheme

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Folded 100%", widthDp = 320, heightDp = 841, showSystemUi = true)
@Preview(name = "Folded 180%", widthDp = 320, heightDp = 841, fontScale = 1.8f, showSystemUi = true)
@Preview(name = "Phone", device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Preview(name = "Unfolded Foldable", device = "spec:width=673dp,height=841dp", showSystemUi = true)
@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true)
@Preview(name = "TV 720p", device = Devices.TV_720p, showSystemUi = true)
annotation class PreviewMultipleConfigurations

@Composable
fun ShowPreview(
    block: @Composable StateMocks.() -> Unit
) {
    AppTheme {
        block(stateMocks)
    }
}
