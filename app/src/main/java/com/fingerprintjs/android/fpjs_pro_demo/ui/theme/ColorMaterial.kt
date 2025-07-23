@file:Suppress("MagicNumber")

package com.fingerprintjs.android.fpjs_pro_demo.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fingerprintjs.android.fpjs_pro_demo.R

internal object AppColors {
    val Black = Color(0xFF141415)
    val Gray50 = Color(0xFFFAFAFA)
    val Gray100 = Color(0xFFE8E8E8)
    val Gray200 = Color(0xFFD0D0D0)
    val Gray300 = Color(0xFFB9B9B9)
    val Gray400 = Color(0xFFA1A1A1)
    val Gray500 = Color(0xFF89898A)
    val Gray600 = Color(0xFF727273)
    val Gray700 = Color(0xFF5B5B5B)
    val Gray800 = Color(0xFF434344)
    val Gray900 = Color(0xFF202021)
    val Orange100 = Color(0xFFFFF1EC)
    val Orange400 = Color(0xFFFF5D22)
    val Red500 = Color(0xFFF42020)
    val White = Color(0xFFFFFFFF)

    // We will use this color as a marker that we need to define some color explicitly and
    // not use the default one
    val UndefinedColor = Color(0xFFFFEB3B)
}

val LightMaterialColorScheme: ColorScheme
    @Composable
    get() = lightColorScheme(
        primary = AppColors.Orange400,
        onPrimary = AppColors.Gray50,
        primaryContainer = AppColors.Orange100,
        onPrimaryContainer = AppColors.Orange400,
        inversePrimary = AppColors.Orange400,
        secondary = AppColors.UndefinedColor,
        onSecondary = AppColors.UndefinedColor,
        secondaryContainer = AppColors.Orange100,
        onSecondaryContainer = AppColors.Orange400,
        tertiary = AppColors.UndefinedColor,
        onTertiary = AppColors.UndefinedColor,
        tertiaryContainer = AppColors.UndefinedColor,
        onTertiaryContainer = AppColors.UndefinedColor,
        background = AppColors.White,
        onBackground = AppColors.Gray900,
        surface = AppColors.White,
        onSurface = AppColors.Gray900,
        surfaceVariant = AppColors.UndefinedColor,
        onSurfaceVariant = AppColors.Gray600,
        surfaceTint = AppColors.Gray300,
        inverseSurface = AppColors.Gray900,
        inverseOnSurface = AppColors.Gray50,
        error = AppColors.Red500,
        onError = AppColors.Gray50,
        errorContainer = AppColors.UndefinedColor,
        onErrorContainer = AppColors.UndefinedColor,
        outline = AppColors.Gray200,
        outlineVariant = AppColors.Gray200,
        scrim = AppColors.UndefinedColor,
        surfaceBright = AppColors.White,
        surfaceContainer = AppColors.Gray100,
        surfaceContainerHigh = AppColors.Gray200,
        surfaceContainerHighest = AppColors.Gray300,
        surfaceContainerLow = AppColors.Gray50,
        surfaceContainerLowest = AppColors.White,
        surfaceDim = AppColors.Gray50,
    )


val DarkMaterialColorScheme: ColorScheme
    @Composable
    get() = darkColorScheme(
        primary = AppColors.Orange400,
        onPrimary = AppColors.Gray900,
        primaryContainer = AppColors.Orange100,
        onPrimaryContainer = AppColors.Orange400,
        inversePrimary = AppColors.Orange400,
        secondary = AppColors.UndefinedColor,
        onSecondary = AppColors.UndefinedColor,
        secondaryContainer = AppColors.Gray800,
        onSecondaryContainer = AppColors.Orange400,
        tertiary = AppColors.UndefinedColor,
        onTertiary = AppColors.UndefinedColor,
        tertiaryContainer = AppColors.UndefinedColor,
        onTertiaryContainer = AppColors.UndefinedColor,
        background = AppColors.Black,
        onBackground = AppColors.Gray50,
        surface = AppColors.Black,
        onSurface = AppColors.Gray50,
        surfaceVariant = AppColors.UndefinedColor,
        onSurfaceVariant = AppColors.Gray300,
        surfaceTint = AppColors.Gray600,
        inverseSurface = AppColors.Gray50,
        inverseOnSurface = AppColors.Gray900,
        error = AppColors.Red500,
        onError = AppColors.Gray900,
        errorContainer = AppColors.UndefinedColor,
        onErrorContainer = AppColors.UndefinedColor,
        outline = AppColors.Gray700,
        outlineVariant = AppColors.Gray700,
        scrim = AppColors.UndefinedColor,
        surfaceBright = AppColors.Black,
        surfaceContainer = AppColors.Gray800,
        surfaceContainerHigh = AppColors.Gray700,
        surfaceContainerHighest = AppColors.Gray600,
        surfaceContainerLow = AppColors.Gray900,
        surfaceContainerLowest = AppColors.Black,
        surfaceDim = AppColors.Gray900,
    )

private typealias PreviewColor = Triple<String, Color, Color>

private val previewColorList: List<PreviewColor>
    @Composable
    get() {
        val light = LightMaterialColorScheme
        val dark = DarkMaterialColorScheme
        return listOf(
            Triple("primary", light.primary, dark.primary),
            Triple("onPrimary", light.onPrimary, dark.onPrimary),
            Triple("primaryContainer", light.primaryContainer, dark.primaryContainer),
            Triple("onPrimaryContainer", light.onPrimaryContainer, dark.onPrimaryContainer),
            Triple("inversePrimary", light.inversePrimary, dark.inversePrimary),
            Triple("secondary", light.secondary, dark.secondary),
            Triple("onSecondary", light.onSecondary, dark.onSecondary),
            Triple("secondaryContainer", light.secondaryContainer, dark.secondaryContainer),
            Triple("onSecondaryContainer", light.onSecondaryContainer, dark.onSecondaryContainer),
            Triple("tertiary", light.tertiary, dark.tertiary),
            Triple("onTertiary", light.onTertiary, dark.onTertiary),
            Triple("tertiaryContainer", light.tertiaryContainer, dark.tertiaryContainer),
            Triple("onTertiaryContainer", light.onTertiaryContainer, dark.onTertiaryContainer),
            Triple("background", light.background, dark.background),
            Triple("onBackground", light.onBackground, dark.onBackground),
            Triple("surface", light.surface, dark.surface),
            Triple("onSurface", light.onSurface, dark.onSurface),
            Triple("surfaceVariant", light.surfaceVariant, dark.surfaceVariant),
            Triple("onSurfaceVariant", light.onSurfaceVariant, dark.onSurfaceVariant),
            Triple("surfaceTint", light.surfaceTint, dark.surfaceTint),
            Triple("inverseSurface", light.inverseSurface, dark.inverseSurface),
            Triple("inverseOnSurface", light.inverseOnSurface, dark.inverseOnSurface),
            Triple("error", light.error, dark.error),
            Triple("onError", light.onError, dark.onError),
            Triple("errorContainer", light.errorContainer, dark.errorContainer),
            Triple("onErrorContainer", light.onErrorContainer, dark.onErrorContainer),
            Triple("outline", light.outline, dark.outline),
            Triple("outlineVariant", light.outlineVariant, dark.outlineVariant),
            Triple("scrim", light.scrim, dark.scrim),
            Triple("surfaceBright", light.surfaceBright, dark.surfaceBright),
            Triple("surfaceContainer", light.surfaceContainer, dark.surfaceContainer),
            Triple("surfaceContainerHigh", light.surfaceContainerHigh, dark.surfaceContainerHigh),
            Triple(
                "surfaceContainerHighest",
                light.surfaceContainerHighest,
                dark.surfaceContainerHighest
            ),
            Triple("surfaceContainerLow", light.surfaceContainerLow, dark.surfaceContainerLow),
            Triple("surfaceContainerLowest", light.surfaceContainerLowest, dark.surfaceContainerLowest),
            Triple("surfaceDim", light.surfaceDim, dark.surfaceDim),
        )
    }

@Composable
private fun List<PreviewColor>.Preview() {
    Column {
        this@Preview.forEach { (name, light, dark) ->
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .width(200.dp),
                    text = "$name: ",
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = light)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = dark)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMaterialThemeColors1() {
    previewColorList.slice(0..11).Preview()
}

@Preview
@Composable
private fun PreviewMaterialThemeColors2() {
    previewColorList.slice(12..23).Preview()
}

@Preview
@Composable
private fun PreviewMaterialThemeColors3() {
    previewColorList.slice(24..35).Preview()
}
