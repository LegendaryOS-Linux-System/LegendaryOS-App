package legendaryos.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object LegendaryColors {
    // ── Tła ────────────────────────────────────────────────────────────────
    val background      = Color(0xFF07080D)
    val sidebar         = Color(0xFF0C0D15)
    val sidebarSelected = Color(0xFF131525)
    val surface         = Color(0xFF0F1018)
    val surfaceElevated = Color(0xFF171825)
    val divider         = Color(0xFF1E2035)

    // ── Paleta phoenix (logo: magenta-violet-blue) ──────────────────────────
    val magenta         = Color(0xFFCC44FF)   // lewe skrzydło
    val violet          = Color(0xFF7722DD)   // ciało feniksa
    val blue            = Color(0xFF3366FF)   // prawe skrzydło
    val cyan            = Color(0xFF44CCFF)   // końcówki skrzydeł
    val pink            = Color(0xFFFF44AA)   // akcent różowy

    // ── Aliasy semantyczne (kompatybilność z istniejącym kodem) ────────────
    val gold            = magenta             // stary kod używał gold → teraz magenta
    val goldDim         = violet
    val accentBlue      = blue               // używane w starym AndroidPage

    // ── Funkcjonalne ───────────────────────────────────────────────────────
    val accent          = Color(0xFFE8720C)   // HackerOS — pomarańczowy
    val textPrimary     = Color(0xFFEEEEFF)
    val textSecondary   = Color(0xFF8888AA)
    val textMuted       = Color(0xFF444466)
    val success         = Color(0xFF44CC77)
    val warning         = Color(0xFFFFAA22)
    val error           = Color(0xFFFF4455)
    val info            = cyan
}

@Composable
fun LegendaryTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary       = LegendaryColors.magenta,
            secondary     = LegendaryColors.blue,
            tertiary      = LegendaryColors.cyan,
            background    = LegendaryColors.background,
            surface       = LegendaryColors.surface,
            onPrimary     = Color.Black,
            onBackground  = LegendaryColors.textPrimary,
            onSurface     = LegendaryColors.textPrimary,
            outline       = LegendaryColors.divider,
        ),
        content = content
    )
}
