package com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * @param countryCode capitalized country code as defined in the ISO 3166-1 alpha 2 standard
 */
@Composable
fun Flag(
    modifier: Modifier = Modifier,
    spriteManager: FlagSpriteManager,
    countryCode: String
) {
    val spriteIndex = Country.getIndex(countryCode)
    if (spriteIndex == null) {
        // draw emoji
    } else {
        // draw sprite
        Image(
            modifier = modifier,
            bitmap = spriteManager.getFlagBitmap(spriteIndex),
            contentDescription = "$countryCode country flag",
        )
    }
}
