package com.fingerprintjs.android.fpjs_pro_demo.ui.component.view.flag

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntRect
import com.fingerprintjs.android.fpjs_pro_demo.R

class FlagSpriteManager(
    private val context: Context,
    private val flagsCount: Int,
) {

    // Load the sprite sheet once and cache it
    private val flagSprite: ImageBitmap by lazy {
        ImageBitmap.imageResource(context.resources, R.drawable.flags)
    }
    private val flagHeight: Int by lazy {
        flagSprite.height / flagsCount
    }
    private val flagWidth: Int by lazy {
        flagSprite.width
    }

    fun getFlagBitmap(index: Int): ImageBitmap {
        if (index !in 0..flagsCount) {
            throw IndexOutOfBoundsException("Flag index must be between 0 and $flagsCount")
        }

        val x = 0
        val y = index * flagHeight
        val rect = IntRect(
            left = x,
            top = y,
            right = x + flagWidth,
            bottom = y + flagHeight
        )
        return Bitmap.createBitmap(
            flagSprite.asAndroidBitmap(),
            rect.left, rect.top,
            rect.width, rect.height
        ).asImageBitmap()
    }
}
