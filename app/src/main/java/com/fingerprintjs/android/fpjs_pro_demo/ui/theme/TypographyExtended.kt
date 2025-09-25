package com.fingerprintjs.android.fpjs_pro_demo.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.fingerprintjs.android.fpjs_pro_demo.R

private val JetbrainsMono = FontFamily(
    Font(R.font.jetbrainsmono_normal, weight = FontWeight.W400),
)

object TypographyExtended {
    val codeNormal: TextStyle = TextStyle(
        fontFamily = JetbrainsMono,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 18.sp,
    )

    val textPrettifiedView: TextStyle = TextStyle(
        fontFamily = JetbrainsMono,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.W400,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Proportional,
            trim = LineHeightStyle.Trim.Both,
        )
    )
}
