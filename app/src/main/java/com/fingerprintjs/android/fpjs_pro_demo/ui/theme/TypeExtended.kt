package com.fingerprintjs.android.fpjs_pro_demo.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.fingerprintjs.android.fpjs_pro_demo.R

private val JetbrainsMono = FontFamily(
    Font(R.font.jetbrainsmono_normal, weight = FontWeight.W400),
)

private val JetbrainsMonoNoWeight = FontFamily(
    Font(R.font.jetbrainsmono_normal),
)

object TypographyExtended {
    val codeNormal: TextStyle = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    )
}
