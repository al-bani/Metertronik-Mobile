package com.alcopoune.metertronik.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = White,
    error = Red,
    surface = Green,
    tertiary = Orange,
    secondary = Blue,
    background = Gray,
    scrim = Black
)

@Composable
fun MetertronikTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
