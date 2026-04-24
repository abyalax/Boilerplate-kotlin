package com.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val LightColorScheme = lightColorScheme(
    primary = BlueMain,
    onPrimary = PureWhite,
    primaryContainer = BlueContainerLight,
    onPrimaryContainer = BlueDark,

    secondary = Gray700,
    onSecondary = PureWhite,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray900,

    tertiary = BlueLight,
    onTertiary = BlueDark,

    error = RedError,
    onError = PureWhite,
    errorContainer = RedErrorContainer,
    onErrorContainer = RedError,

    background = PureWhite,
    onBackground = PureBlack,

    surface = Gray100,
    onSurface = PureBlack,
    surfaceVariant = Gray200,
    onSurfaceVariant = Gray700,

    outline = Gray500,
    outlineVariant = Gray200,

    scrim = PureBlack,
    inverseSurface = Gray900,
    inverseOnSurface = PureWhite,
    inversePrimary = BlueLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = BlueDark,
    primaryContainer = BlueContainerDark,
    onPrimaryContainer = BlueContainerLight,

    secondary = Gray500,
    onSecondary = PureBlack,
    secondaryContainer = Gray700,
    onSecondaryContainer = Gray200,

    tertiary = BlueMain,
    onTertiary = PureWhite,

    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),

    background = PureBlack,
    onBackground = PureWhite,

    surface = Gray900,
    onSurface = PureWhite,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray200,

    outline = Gray500,
    outlineVariant = Gray700,

    scrim = PureBlack,
    inverseSurface = PureWhite,
    inverseOnSurface = PureBlack,
    inversePrimary = BlueMain
)

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,

        content = content
    )
}