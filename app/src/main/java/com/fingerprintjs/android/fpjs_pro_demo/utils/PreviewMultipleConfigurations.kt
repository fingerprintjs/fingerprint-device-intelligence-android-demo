package com.fingerprintjs.android.fpjs_pro_demo.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Devices.FOLDABLE
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Folded 100%", widthDp = 320, heightDp = 841, showSystemUi = true)
@Preview(name = "Folded 180%", widthDp = 320, heightDp = 841, fontScale = 1.8f, showSystemUi = true)
@Preview(name = "Phone", device = PHONE, showSystemUi = true)
@Preview(name = "Unfolded Foldable", device = FOLDABLE, showSystemUi = true)
@Preview(name = "Tablet", device = TABLET, showSystemUi = true)
@Preview(name = "TV 720p", device = Devices.TV_720p, showSystemUi = true)
annotation class PreviewMultipleConfigurations
