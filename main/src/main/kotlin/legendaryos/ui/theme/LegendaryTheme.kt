package legendaryos.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── LegendaryOS Design Tokens ─────────────────────────────────────────────────
object LegendaryColors {
    val background      = Color(0xFF0D0F14)
    val sidebar         = Color(0xFF111318)
    val sidebarSelected = Color(0xFF1C1F2A)
    val surface         = Color(0xFF161920)
    val surfaceElevated = Color(0xFF1C1F2A)
    val divider         = Color(0xFF2A2D38)

    val gold            = Color(0xFFD4A843)
    val goldDim         = Color(0xFF8B6C27)
    val accent          = Color(0xFFE8720C)   // HackerOS orange
    val accentBlue      = Color(0xFF3A8EF6)   // Android / connect

    val textPrimary     = Color(0xFFE8EAF0)
    val textSecondary   = Color(0xFF8D93A5)
    val textMuted       = Color(0xFF565C70)

    val success         = Color(0xFF4CAF50)
    val warning         = Color(0xFFFF9800)
    val error           = Color(0xFFEF5350)
    val info            = Color(0xFF29B6F6)
}

@Composable
fun LegendaryTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary         = LegendaryColors.gold,
        secondary       = LegendaryColors.accent,
        background      = LegendaryColors.background,
        surface         = LegendaryColors.surface,
        onPrimary       = Color.Black,
        onBackground    = LegendaryColors.textPrimary,
        onSurface       = LegendaryColors.textPrimary,
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}
