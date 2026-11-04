package ru.danilin52.historyreference.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Brown = Color(0xFF6D4C41)
private val BrownDark = Color(0xFF4E342E)
private val Sand = Color(0xFFFFF8F3)

private val LightColors = lightColorScheme(
    primary = Brown,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7CCC8),
    onPrimaryContainer = BrownDark,
    secondary = Color(0xFF8D6E63),
    background = Sand,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFBCAAA4),
    secondary = Color(0xFFA1887F)
)

@Composable
fun HistoryReferenceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}