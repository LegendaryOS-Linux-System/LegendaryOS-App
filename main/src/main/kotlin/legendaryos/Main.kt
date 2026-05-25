package legendaryos

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import legendaryos.ui.LegendaryOSApp

fun main() = application {
    val state = rememberWindowState(width = 1280.dp, height = 800.dp)
    Window(
        onCloseRequest = ::exitApplication,
        title = "LegendaryOS App",
        state = state,
    ) {
        LegendaryOSApp()
    }
}
