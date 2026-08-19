package com.example.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = PurpleAccent,
        onPrimary = Color.White,
        primaryContainer = CardSurfaceVariant,
        onPrimaryContainer = TextPrimary,
        secondary = VelvetPurple,
        onSecondary = Color.White,
        tertiary = NeonPink,
        background = DarkCanvas,
        onBackground = TextPrimary,
        surface = CardSurface,
        onSurface = TextPrimary,
        surfaceVariant = CardSurfaceVariant,
        onSurfaceVariant = TextSecondary,
        outline = DividerColor,
    )

@Composable
fun AuraStudioTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = DarkColorScheme, typography = Typography(), content = content)
}
