package com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
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
        Text(
            modifier = modifier,
            text = countryCode.toEmojiFlag()
        )
    } else {
        // draw sprite
        Image(
            modifier = modifier,
            bitmap = spriteManager.getFlagBitmap(spriteIndex),
            contentDescription = "$countryCode country flag",
        )
    }
}

private fun String.toEmojiFlag(): String {
    if (length != 2) return this

    val firstLetter = Character.codePointAt(this, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(this, 1) - 0x41 + 0x1F1E6

    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}
