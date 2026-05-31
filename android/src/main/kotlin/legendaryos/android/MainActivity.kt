package legendaryos.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.android.bridge.LinuxBridge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LegendaryAndroidTheme {
                LegendaryAndroidApp()
            }
        }
    }
}

// ── Paleta kolorów — styl logo (pixel phoenix, fiolet-magenta-niebieski) ──────
object AndroidColors {
    val background    = Color(0xFF07080D)
    val surface       = Color(0xFF0F1018)
    val surfaceHigh   = Color(0xFF171825)
    val divider       = Color(0xFF1E2035)

    // Główne kolory gradientu logo
    val magenta       = Color(0xFFCC44FF)
    val violet        = Color(0xFF7722DD)
    val blue          = Color(0xFF3366FF)
    val cyan          = Color(0xFF44CCFF)
    val pink          = Color(0xFFFF44AA)

    // Akcenty funkcjonalne
    val gold          = Color(0xFFD4A843)
    val orange        = Color(0xFFE8720C)
    val success       = Color(0xFF44CC77)
    val warning       = Color(0xFFFFAA22)
    val error         = Color(0xFFFF4455)

    val textPrimary   = Color(0xFFEEEEFF)
    val textSecondary = Color(0xFF8888AA)
    val textMuted     = Color(0xFF444466)

    // Gradienty
    val gradientLogo  = Brush.linearGradient(listOf(magenta, violet, blue))
    val gradientWarm  = Brush.linearGradient(listOf(pink, magenta, violet))
    val gradientCold  = Brush.linearGradient(listOf(blue, cyan))
    val gradientGlow  = Brush.radialGradient(
        listOf(violet.copy(alpha = 0.3f), Color.Transparent),
                                             radius = 400f
    )
}

// ── Motyw ─────────────────────────────────────────────────────────────────────
@Composable
fun LegendaryAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary          = AndroidColors.magenta,
            secondary        = AndroidColors.blue,
            tertiary         = AndroidColors.cyan,
            background       = AndroidColors.background,
            surface          = AndroidColors.surface,
            surfaceVariant   = AndroidColors.surfaceHigh,
            onPrimary        = Color.Black,
            onBackground     = AndroidColors.textPrimary,
            onSurface        = AndroidColors.textPrimary,
            outline          = AndroidColors.divider,
        ),
        content = content
    )
}

// ── Nawigacja ─────────────────────────────────────────────────────────────────
enum class AndroidNav(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
) {
    Dashboard ("Główna",    Icons.Filled.Dashboard,      AndroidColors.magenta),
    Linux     ("Linux",     Icons.Filled.Computer,       AndroidColors.blue),
    HackerOS  ("HackerOS",  Icons.Filled.Security,       AndroidColors.orange),
    Files     ("Pliki",     Icons.Filled.Folder,         AndroidColors.cyan),
    Terminal  ("Terminal",  Icons.Filled.Terminal,       AndroidColors.violet),
    Monitor   ("Monitor",   Icons.Filled.BarChart,       AndroidColors.pink),
    Settings  ("Opcje",     Icons.Filled.Settings,       AndroidColors.gold),
}

// ── Root aplikacji ────────────────────────────────────────────────────────────
@Composable
fun LegendaryAndroidApp() {
    var currentNav by remember { mutableStateOf(AndroidNav.Dashboard) }
    var linuxConnected by remember { mutableStateOf(false) }
    var linuxHost by remember { mutableStateOf("192.168.1.10") }

    Scaffold(
        containerColor = AndroidColors.background,
        bottomBar = {
            LegendaryBottomBar(currentNav) { currentNav = it }
        }
    ) { padding ->
        Box(
            Modifier
            .fillMaxSize()
            .padding(padding)
            .drawBehind {
                // Pixel-glow w tle
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(AndroidColors.violet.copy(0.12f), Color.Transparent)
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(size.width * 0.5f, size.height * 0.1f)
                )
            }
        ) {
            AnimatedContent(
                targetState = currentNav,
                transitionSpec = {
                    fadeIn(tween(250, easing = EaseOutCubic)) +
                    slideInVertically(tween(250, easing = EaseOutCubic)) { it / 12 } togetherWith
                    fadeOut(tween(180))
                }
            ) { nav ->
                when (nav) {
                    AndroidNav.Dashboard -> AndroidDashboard(linuxConnected, linuxHost) { linuxConnected = !linuxConnected }
                    AndroidNav.Linux     -> LinuxControlScreen(linuxConnected, linuxHost)
                    AndroidNav.HackerOS  -> HackerOSScreen()
                    AndroidNav.Files     -> FilesScreen(linuxConnected)
                    AndroidNav.Terminal  -> TerminalScreen(linuxConnected)
                    AndroidNav.Monitor   -> MonitorScreen(linuxConnected)
                    AndroidNav.Settings  -> SettingsScreen(linuxHost) { linuxHost = it }
                }
            }
        }
    }
}

@Composable
fun LegendaryBottomBar(current: AndroidNav, onSelect: (AndroidNav) -> Unit) {
    Box(
        Modifier
        .fillMaxWidth()
        .background(AndroidColors.surface)
        .drawBehind {
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        AndroidColors.magenta.copy(0.6f),
                           AndroidColors.violet.copy(0.4f),
                           AndroidColors.blue.copy(0.6f)
                    )
                ),
                start = Offset(0f, 0f),
                     end = Offset(size.width, 0f),
                     strokeWidth = 1.5f
            )
        }
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            AndroidNav.values().forEach { nav ->
                val sel = current == nav
                NavigationBarItem(
                    selected = sel,
                    onClick = { onSelect(nav) },
                                  icon = {
                                      Box(contentAlignment = Alignment.Center) {
                                          if (sel) {
                                              Box(
                                                  Modifier
                                                  .size(36.dp)
                                                  .background(
                                                      nav.color.copy(0.18f),
                                                              RoundedCornerShape(10.dp)
                                                  )
                                              )
                                          }
                                          Icon(
                                              nav.icon,
                                               contentDescription = nav.label,
                                               modifier = Modifier.size(20.dp),
                                               tint = if (sel) nav.color else AndroidColors.textMuted
                                          )
                                      }
                                  },
                                  label = {
                                      Text(
                                          nav.label,
                                           fontSize = 9.sp,
                                           color = if (sel) nav.color else AndroidColors.textMuted
                                      )
                                  },
                                  colors = NavigationBarItemDefaults.colors(
                                      indicatorColor = Color.Transparent
                                  )
                )
            }
        }
    }
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
@Composable
fun AndroidDashboard(connected: Boolean, host: String, onToggle: () -> Unit) {
    val scroll = rememberScrollState()
    Column(
        Modifier
        .fillMaxSize()
        .verticalScroll(scroll)
        .padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        // Header — Logo style
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "LEGENDARY",
                     style = androidx.compose.ui.text.TextStyle(
                         brush = AndroidColors.gradientLogo,
                         fontSize = 22.sp,
                         fontWeight = FontWeight.ExtraBold,
                         letterSpacing = 2.sp
                     )
                )
                Text(
                    "OS  APP",
                     color = AndroidColors.textSecondary,
                     fontSize = 11.sp,
                     letterSpacing = 4.sp
                )
            }
            ConnectionBadge(connected)
        }

        // Connect card z gradientem
        LegendaryCard(
            borderBrush = if (connected)
            Brush.linearGradient(listOf(AndroidColors.success, AndroidColors.cyan))
            else
                Brush.linearGradient(listOf(AndroidColors.magenta, AndroidColors.violet))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (connected) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                            contentDescription = null,
                         tint = if (connected) AndroidColors.success else AndroidColors.magenta,
                         modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            if (connected) "Połączony z LegendaryOS" else "Brak połączenia",
                                color = AndroidColors.textPrimary,
                             fontWeight = FontWeight.SemiBold,
                             fontSize = 14.sp
                        )
                        Text(
                            if (connected) host else "Skonfiguruj w Ustawieniach",
                                color = AndroidColors.textSecondary,
                             fontSize = 11.sp
                        )
                    }
                }
                GradientButton(
                    onClick = onToggle,
                    brush = if (connected)
                    Brush.linearGradient(listOf(AndroidColors.error, Color(0xFFAA2233)))
                    else
                        AndroidColors.gradientLogo,
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (connected) Icons.Filled.LinkOff else Icons.Filled.Link,
                            contentDescription = null,
                         modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (connected) "Rozłącz" else "Połącz z Linux")
                }
            }
        }

        // Szybkie akcje — 2x3 grid
        Text("Szybkie akcje", color = AndroidColors.textSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
        val tiles = listOf(
            TileData("Schowek",     Icons.Filled.ContentPaste, AndroidColors.magenta),
                           TileData("Terminal",    Icons.Filled.Terminal,     AndroidColors.blue),
                           TileData("Mirror",      Icons.Filled.ScreenShare,  AndroidColors.cyan),
                           TileData("SMS na PC",   Icons.Filled.Message,      AndroidColors.violet),
                           TileData("Kamera",      Icons.Filled.CameraAlt,    AndroidColors.pink),
                           TileData("Hotspot",     Icons.Filled.Wifi,         AndroidColors.orange),
        )
        tiles.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tile ->
                    QuickTile(Modifier.weight(1f), tile, connected)
                }
            }
        }

        // Status systemowy (mock)
        Text("Status systemu", color = AndroidColors.textSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
        if (connected) {
            StatusMetricsRow()
        } else {
            LegendaryCard {
                Text(
                    "Połącz się aby zobaczyć metryki LegendaryOS",
                     color = AndroidColors.textMuted,
                     fontSize = 12.sp
                )
            }
        }

        // Ostatnie zdarzenia
        Text("Log zdarzeń", color = AndroidColors.textSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
        EventLogCard()
    }
}

data class TileData(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
fun QuickTile(modifier: Modifier, tile: TileData, enabled: Boolean) {
    val alpha = if (enabled) 1f else 0.35f
    Box(
        modifier
        .aspectRatio(1f)
        .alpha(alpha)
        .clip(RoundedCornerShape(14.dp))
        .background(AndroidColors.surfaceHigh)
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(tile.color.copy(0.4f), tile.color.copy(0.1f))),
                shape = RoundedCornerShape(14.dp)
        )
        .clickable(enabled = enabled) {}
        .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                Modifier
                .size(32.dp)
                .background(tile.color.copy(0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tile.icon, contentDescription = tile.label, tint = tile.color, modifier = Modifier.size(18.dp))
            }
            Text(tile.label, color = AndroidColors.textSecondary, fontSize = 9.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun StatusMetricsRow() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            Triple("CPU", 42f, AndroidColors.magenta),
               Triple("RAM", 67f, AndroidColors.blue),
               Triple("Dysk", 38f, AndroidColors.cyan),
        ).forEach { (label, value, color) ->
            LegendaryCard(modifier = Modifier.weight(1f)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(label, color = AndroidColors.textMuted, fontSize = 10.sp)
                    Text(
                        "${value.toInt()}%",
                         color = color,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = { value / 100f },
                        modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                                            color = color,
                                            trackColor = AndroidColors.divider
                    )
                }
            }
        }
    }
}

@Composable
fun EventLogCard() {
    LegendaryCard(
        borderBrush = Brush.linearGradient(listOf(AndroidColors.violet.copy(0.3f), AndroidColors.blue.copy(0.2f)))
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                Triple(AndroidColors.success,  "✓", "System LegendaryOS uruchomiony"),
                   Triple(AndroidColors.blue,     "⚡", "ADB serwer aktywny (port 5037)"),
                   Triple(AndroidColors.magenta,  "↑", "Sprawdzanie aktualizacji bootc"),
                   Triple(AndroidColors.textMuted,"·", "Brak nowych aktualizacji"),
                   Triple(AndroidColors.warning,  "!", "Urządzenie Android nie wykryte przez ADB"),
            ).forEach { (color, icon, msg) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, color = color, fontSize = 12.sp, modifier = Modifier.width(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(msg, color = AndroidColors.textSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

// ── Linux Control ─────────────────────────────────────────────────────────────
@Composable
fun LinuxControlScreen(connected: Boolean, host: String) {
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        PageHeader("Sterowanie Linux", "LegendaryOS · bootc", Icons.Filled.Computer, AndroidColors.blue)

        if (!connected) {
            DisconnectedBanner()
            return
        }

        // bootc sekcja
        SectionLabel("System OCI / bootc")
        LegendaryCard(borderBrush = Brush.linearGradient(listOf(AndroidColors.blue, AndroidColors.cyan))) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).background(AndroidColors.success, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("LegendaryOS aktualny", color = AndroidColors.textPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text("ghcr.io/LegendaryOS/LegendaryOS:latest", color = AndroidColors.textMuted, fontSize = 10.sp)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GradientButton(
                        onClick = { LinuxBridge.sendCommand("bootc upgrade") },
                                   brush = AndroidColors.gradientCold,
                                   modifier = Modifier.weight(1f)
                    ) { Text("bootc upgrade", fontSize = 12.sp) }
                    OutlinedButton(
                        onClick = { LinuxBridge.sendCommand("bootc rollback") },
                                   modifier = Modifier.weight(1f),
                                   border = BorderStroke(1.dp, AndroidColors.divider)
                    ) { Text("Rollback", color = AndroidColors.textSecondary, fontSize = 12.sp) }
                }
            }
        }

        // Zasilanie
        SectionLabel("Zasilanie")
        val powerActions = listOf(
            ActionData("Zablokuj ekran",     Icons.Filled.Lock,             AndroidColors.blue,    "loginctl lock-session"),
                                  ActionData("Uśpij komputer",     Icons.Filled.NightShelter,     AndroidColors.violet,  "systemctl suspend"),
                                  ActionData("Hibernacja",         Icons.Filled.BatteryAlert,     AndroidColors.magenta, "systemctl hibernate"),
                                  ActionData("Restart",            Icons.Filled.Refresh,          AndroidColors.cyan,    "systemctl reboot"),
                                  ActionData("Wyłącz",             Icons.Filled.PowerSettingsNew, AndroidColors.error,   "systemctl poweroff"),
        )
        powerActions.forEach { action ->
            ActionRow(action) { LinuxBridge.sendCommand(it) }
        }

        // Media
        SectionLabel("Multimedia")
        val mediaActions = listOf(
            ActionData("Wycisz audio",       Icons.Filled.VolumeMute,  AndroidColors.pink,   "pactl set-sink-mute @DEFAULT_SINK@ toggle"),
                                  ActionData("Głośność +10%",      Icons.Filled.VolumeUp,    AndroidColors.cyan,   "pactl set-sink-volume @DEFAULT_SINK@ +10%"),
                                  ActionData("Głośność -10%",      Icons.Filled.VolumeDown,  AndroidColors.blue,   "pactl set-sink-volume @DEFAULT_SINK@ -10%"),
                                  ActionData("Pauza/Play",         Icons.Filled.PlayArrow,   AndroidColors.magenta,"playerctl play-pause"),
                                  ActionData("Następny utwór",     Icons.Filled.SkipNext,    AndroidColors.violet, "playerctl next"),
        )
        mediaActions.forEach { action ->
            ActionRow(action) { LinuxBridge.sendCommand(it) }
        }

        // Szybkie polecenia
        SectionLabel("Szybkie polecenia shell")
        LegendaryCard {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "flatpak update --noninteractive",
                    "journalctl -n 50 --no-pager",
                    "df -h",
                    "free -h",
                    "systemctl status",
                ).forEach { cmd ->
                    Row(
                        Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AndroidColors.background)
                        .clickable { LinuxBridge.sendCommand(cmd) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$ $cmd",
                             color = AndroidColors.cyan,
                             fontSize = 11.sp,
                             fontFamily = FontFamily.Monospace,
                             modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = AndroidColors.textMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

data class ActionData(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val command: String
)

@Composable
fun ActionRow(action: ActionData, onAction: (String) -> Unit) {
    LegendaryCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
            .fillMaxWidth()
            .clickable { onAction(action.command) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                .size(36.dp)
                .background(action.color.copy(0.15f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(action.icon, contentDescription = null, tint = action.color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(action.label, color = AndroidColors.textPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AndroidColors.textMuted, modifier = Modifier.size(16.dp))
        }
    }
}

// ── HackerOS ─────────────────────────────────────────────────────────────────
@Composable
fun HackerOSScreen() {
    var sshConnected by remember { mutableStateOf(false) }
    var host by remember { mutableStateOf("192.168.1.20") }
    var user by remember { mutableStateOf("hacker") }
    val scroll = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        PageHeader("HackerOS Bridge", "SSH · VNC · Narzędzia", Icons.Filled.Security, AndroidColors.orange)

        // SSH Config
        LegendaryCard(
            borderBrush = Brush.linearGradient(listOf(AndroidColors.orange, AndroidColors.magenta))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Konfiguracja SSH", color = AndroidColors.textSecondary, fontSize = 11.sp, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = host, onValueChange = { host = it },
                    label = { Text("Host / IP") },
                                  modifier = Modifier.fillMaxWidth(),
                                  singleLine = true,
                                  colors = legendaryTextFieldColors(AndroidColors.orange)
                )
                OutlinedTextField(
                    value = user, onValueChange = { user = it },
                    label = { Text("Użytkownik") },
                                  modifier = Modifier.fillMaxWidth(),
                                  singleLine = true,
                                  colors = legendaryTextFieldColors(AndroidColors.orange)
                )
                GradientButton(
                    onClick = { sshConnected = !sshConnected },
                    brush = if (sshConnected)
                    Brush.linearGradient(listOf(AndroidColors.error, Color(0xFFAA2233)))
                    else
                        Brush.linearGradient(listOf(AndroidColors.orange, AndroidColors.magenta)),
                               modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (sshConnected) Icons.Filled.LinkOff else Icons.Filled.Link,
                            contentDescription = null, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (sshConnected) "Rozłącz SSH" else "Połącz SSH")
                }
            }
        }

        SectionLabel("Funkcje HackerOS")
        val features = listOf(
            TileData("SSH Shell",      Icons.Filled.Terminal,      AndroidColors.orange),
                              TileData("SCP Transfer",   Icons.Filled.FileCopy,      AndroidColors.gold),
                              TileData("VNC Ekran",      Icons.Filled.DesktopWindows,AndroidColors.magenta),
                              TileData("Port Forward",   Icons.Filled.Router,        AndroidColors.violet),
                              TileData("Nmap Scan",      Icons.Filled.NetworkCheck,  AndroidColors.blue),
                              TileData("Narzędzia",      Icons.Filled.BugReport,     AndroidColors.error),
        )
        features.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { tile ->
                    QuickTile(Modifier.weight(1f), tile, sshConnected)
                }
            }
        }

        // Info
        LegendaryCard(
            borderBrush = Brush.linearGradient(listOf(AndroidColors.orange.copy(0.5f), AndroidColors.violet.copy(0.3f)))
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = AndroidColors.orange, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "HackerOS to dystrybucja Linuksa skupiona na cyberbezpieczeństwie. " +
                    "LegendaryOS App umożliwia pełną integrację przez SSH, VNC i mostek plików.",
                     color = AndroidColors.textSecondary,
                     fontSize = 11.sp,
                     lineHeight = 16.sp
                )
            }
        }
    }
}

// ── Files ─────────────────────────────────────────────────────────────────────
@Composable
fun FilesScreen(connected: Boolean) {
    var selectedSource by remember { mutableStateOf("LegendaryOS") }
    var currentPath by remember { mutableStateOf("/home/user") }
    val scroll = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        PageHeader("Transfer Plików", "Android ↔ Linux ↔ HackerOS", Icons.Filled.Folder, AndroidColors.cyan)

        if (!connected) { DisconnectedBanner(); return }

        // Source tabs
        val sources = listOf("LegendaryOS", "HackerOS (SSH)", "Android")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            sources.forEach { src ->
                val sel = src == selectedSource
                Box(
                    Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (sel) AndroidColors.cyan.copy(0.2f) else AndroidColors.surfaceHigh)
                    .border(1.dp, if (sel) AndroidColors.cyan.copy(0.5f) else Color.Transparent, RoundedCornerShape(8.dp))
                    .clickable { selectedSource = src }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(src, color = if (sel) AndroidColors.cyan else AndroidColors.textSecondary, fontSize = 11.sp)
                }
            }
        }

        // Path
        OutlinedTextField(
            value = currentPath, onValueChange = { currentPath = it },
            label = { Text("Ścieżka") },
                          modifier = Modifier.fillMaxWidth(),
                          singleLine = true,
                          leadingIcon = { Icon(Icons.Filled.FolderOpen, contentDescription = null) },
                          colors = legendaryTextFieldColors(AndroidColors.cyan)
        )

        // File list
        LegendaryCard {
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                val entries = listOf(
                    Pair("📁", "Documents"), Pair("📁", "Downloads"), Pair("📁", "Pictures"),
                                     Pair("📁", "Music"), Pair("📁", "Videos"), Pair("📄", "README.md"),
                                     Pair("📄", ".bashrc"), Pair("⚙", "LegendaryOS.conf"), Pair("📄", "legendary.log")
                )
                entries.forEachIndexed { i, (ico, name) ->
                    Row(
                        Modifier
                        .fillMaxWidth()
                        .clickable {}
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ico, fontSize = 15.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(name, color = AndroidColors.textPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AndroidColors.textMuted, modifier = Modifier.size(14.dp))
                    }
                    if (i < entries.lastIndex) {
                        Divider(color = AndroidColors.divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 8.dp))
                    }
                }
            }
        }

        // Transfer actions
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GradientButton(
                onClick = {},
                brush = AndroidColors.gradientLogo,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Upload, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text("Wyślij", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                           border = BorderStroke(1.dp, AndroidColors.cyan.copy(0.5f))
            ) {
                Icon(Icons.Filled.Download, contentDescription = null, tint = AndroidColors.cyan, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(4.dp))
                Text("Pobierz", color = AndroidColors.cyan, fontSize = 12.sp)
            }
        }
    }
}

// ── Terminal ──────────────────────────────────────────────────────────────────
@Composable
fun TerminalScreen(connected: Boolean) {
    var input by remember { mutableStateOf("") }
    val history = remember {
        mutableStateListOf(
            "LegendaryOS Mobile Terminal v1.1",
            "Połączony z: LegendaryOS (Fedora 44 · bootc)",
                           "Wpisz 'help' aby zobaczyć dostępne polecenia.",
                           ""
        )
    }
    val scroll = rememberScrollState()

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Spacer(Modifier.height(4.dp))
        PageHeader("Terminal", "Zdalny shell LegendaryOS", Icons.Filled.Terminal, AndroidColors.violet)

        if (!connected) {
            DisconnectedBanner()
        } else {
            Box(
                Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF050608))
                .border(1.dp, AndroidColors.violet.copy(0.3f), RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
                Column(
                    Modifier
                    .fillMaxSize()
                    .verticalScroll(scroll)
                ) {
                    history.forEach { line ->
                        Text(
                            line,
                             color = when {
                                 line.startsWith("$") -> AndroidColors.magenta
                                 line.startsWith("✓") || line.startsWith("●") -> AndroidColors.success
                                 line.startsWith("✗") || line.startsWith("Błąd") -> AndroidColors.error
                                 line.startsWith("Legend") -> AndroidColors.cyan
                                 else -> Color(0xFFAABBCC)
                             },
                             fontSize = 11.sp,
                             fontFamily = FontFamily.Monospace,
                             lineHeight = 16.sp
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$", color = AndroidColors.magenta, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                                  placeholder = { Text("polecenie...", color = AndroidColors.textMuted) },
                                  singleLine = true,
                                  colors = legendaryTextFieldColors(AndroidColors.violet)
                )
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            history.add("$ $input")
                            history.add("(wysłano do LegendaryOS)")
                            history.add("")
                            input = ""
                        }
                    },
                    modifier = Modifier
                    .background(
                        Brush.linearGradient(listOf(AndroidColors.magenta, AndroidColors.violet)),
                                RoundedCornerShape(10.dp)
                    )
                ) {
                    Icon(Icons.Filled.Send, contentDescription = "Wyślij", tint = Color.White)
                }
            }
        }
    }
}

// ── Monitor ───────────────────────────────────────────────────────────────────
@Composable
fun MonitorScreen(connected: Boolean) {
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        PageHeader("Monitor systemu", "Live metryki LegendaryOS", Icons.Filled.BarChart, AndroidColors.pink)

        if (!connected) { DisconnectedBanner(); return }

        // Metryki
        SectionLabel("Zasoby sprzętowe")
        listOf(
            MetricData("CPU", 42f, "Intel Core i7", AndroidColors.magenta),
               MetricData("RAM", 67f, "8.1 / 16 GB", AndroidColors.blue),
               MetricData("Dysk /", 38f, "142 / 512 GB", AndroidColors.cyan),
               MetricData("GPU", 22f, "NVIDIA RTX", AndroidColors.violet),
               MetricData("Temp CPU", 58f, "58°C", AndroidColors.orange),
        ).forEach { metric ->
            LargeMetricCard(metric)
        }

        SectionLabel("Procesy (top 5)")
        LegendaryCard {
            Column {
                listOf(
                    Triple("plasmashell", "12%", "234 MB"),
                       Triple("kwin_wayland", "8%", "156 MB"),
                       Triple("adb server", "2%", "45 MB"),
                       Triple("LegendaryOS-App", "4%", "320 MB"),
                       Triple("systemd", "0.5%", "12 MB"),
                ).forEachIndexed { i, (proc, cpu, mem) ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${i+1}", color = AndroidColors.textMuted, fontSize = 10.sp, modifier = Modifier.width(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(proc, color = AndroidColors.textPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(cpu, color = AndroidColors.magenta, fontSize = 11.sp, modifier = Modifier.width(40.dp))
                        Text(mem, color = AndroidColors.blue, fontSize = 11.sp)
                    }
                    if (i < 4) Divider(color = AndroidColors.divider, thickness = 0.5.dp)
                }
            }
        }

        SectionLabel("Serwisy systemd")
        LegendaryCard {
            Column {
                listOf(
                    Triple("NetworkManager",      true,  "aktywny"),
                       Triple("bluetooth",           true,  "aktywny"),
                       Triple("adb.service",         true,  "aktywny"),
                       Triple("sshd",                false, "zatrzymany"),
                       Triple("cups",                false, "wyłączony"),
                ).forEachIndexed { i, (svc, active, status) ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(6.dp).background(
                            if (active) AndroidColors.success else AndroidColors.textMuted, CircleShape
                        ))
                        Spacer(Modifier.width(10.dp))
                        Text(svc, color = AndroidColors.textPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(status, color = if (active) AndroidColors.success else AndroidColors.textMuted, fontSize = 10.sp)
                    }
                    if (i < 4) Divider(color = AndroidColors.divider, thickness = 0.5.dp)
                }
            }
        }
    }
}

data class MetricData(val label: String, val value: Float, val detail: String, val color: Color)

@Composable
fun LargeMetricCard(metric: MetricData) {
    LegendaryCard {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(metric.label, color = AndroidColors.textMuted, fontSize = 10.sp)
                Text(
                    metric.detail,
                     color = AndroidColors.textSecondary,
                     fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${metric.value.toInt()}%",
                     color = metric.color,
                     fontSize = 18.sp,
                     fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { metric.value / 100f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = metric.color,
                                trackColor = AndroidColors.divider
        )
    }
}

// ── Settings ─────────────────────────────────────────────────────────────────
@Composable
fun SettingsScreen(linuxHost: String, onHostChange: (String) -> Unit) {
    var autoConnect by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }
    var smsForward by remember { mutableStateOf(false) }
    var host by remember { mutableStateOf(linuxHost) }
    var port by remember { mutableStateOf("2222") }
    val scroll = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(16.dp),
           verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(Modifier.height(4.dp))
        PageHeader("Ustawienia", "Konfiguracja LegendaryOS App", Icons.Filled.Settings, AndroidColors.gold)

        SectionLabel("Połączenie")
        LegendaryCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = host, onValueChange = { host = it },
                    label = { Text("Host / IP LegendaryOS") },
                                  modifier = Modifier.fillMaxWidth(),
                                  singleLine = true,
                                  colors = legendaryTextFieldColors(AndroidColors.gold)
                )
                OutlinedTextField(
                    value = port, onValueChange = { port = it },
                    label = { Text("Port SSH") },
                                  modifier = Modifier.fillMaxWidth(),
                                  singleLine = true,
                                  colors = legendaryTextFieldColors(AndroidColors.gold)
                )
                GradientButton(
                    onClick = { onHostChange(host) },
                               brush = Brush.linearGradient(listOf(AndroidColors.gold, AndroidColors.orange)),
                               modifier = Modifier.fillMaxWidth()
                ) { Text("Zapisz połączenie", color = Color.Black) }
            }
        }

        SectionLabel("Zachowanie")
        LegendaryCard {
            Column {
                ToggleRow("Auto-połącz przy starcie", autoConnect, AndroidColors.magenta) { autoConnect = it }
                Divider(color = AndroidColors.divider, thickness = 0.5.dp)
                ToggleRow("Powiadomienia Android → PC", notifications, AndroidColors.blue) { notifications = it }
                Divider(color = AndroidColors.divider, thickness = 0.5.dp)
                ToggleRow("Przekieruj SMS na Linux", smsForward, AndroidColors.violet) { smsForward = it }
            }
        }

        SectionLabel("O aplikacji")
        LegendaryCard(borderBrush = AndroidColors.gradientLogo) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "Wersja"    to "1.1.0 Android",
                    "Projekt"   to "LegendaryOS",
                    "Platforma" to "Jetpack Compose",
                    "Licencja"  to "GPLv3",
                    "Build"     to "Kotlin 2.0 · AGP 8.6",
                ).forEach { (k, v) ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(k, color = AndroidColors.textMuted, fontSize = 11.sp, modifier = Modifier.width(90.dp))
                        Text(v, color = AndroidColors.textPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ── Helpers / komponenty wspólne ─────────────────────────────────────────────

@Composable
fun ConnectionBadge(connected: Boolean) {
    Box(
        Modifier
        .background(
            if (connected) AndroidColors.success.copy(0.15f) else AndroidColors.magenta.copy(0.15f),
                RoundedCornerShape(20.dp)
        )
        .border(
            1.dp,
            if (connected) AndroidColors.success.copy(0.4f) else AndroidColors.magenta.copy(0.4f),
                RoundedCornerShape(20.dp)
        )
        .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).background(
                if (connected) AndroidColors.success else AndroidColors.magenta, CircleShape
            ))
            Spacer(Modifier.width(5.dp))
            Text(
                if (connected) "Online" else "Offline",
                    color = if (connected) AndroidColors.success else AndroidColors.magenta,
                 fontSize = 11.sp
            )
        }
    }
}

@Composable
fun LegendaryCard(
    modifier: Modifier = Modifier,
    borderBrush: Brush = Brush.linearGradient(listOf(AndroidColors.divider, AndroidColors.divider)),
                  content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(AndroidColors.surfaceHigh)
        .border(1.dp, borderBrush, RoundedCornerShape(14.dp))
        .padding(14.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun GradientButton(
    onClick: () -> Unit,
                   brush: Brush,
                   modifier: Modifier = Modifier,
                   content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier
        .clip(RoundedCornerShape(10.dp))
        .background(brush)
        .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun PageHeader(
    title: String, sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier
            .size(42.dp)
            .background(color.copy(0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Column {
            Text(title, color = AndroidColors.textPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = AndroidColors.textMuted, fontSize = 11.sp)
        }
    }
}

@Composable
fun SectionLabel(label: String) {
    Text(
        label.uppercase(),
         color = AndroidColors.textMuted,
         fontSize = 10.sp,
         letterSpacing = 1.5.sp,
         fontWeight = FontWeight.Medium
    )
}

@Composable
fun DisconnectedBanner() {
    LegendaryCard(
        borderBrush = Brush.linearGradient(listOf(AndroidColors.magenta.copy(0.4f), AndroidColors.violet.copy(0.3f)))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.WifiOff, contentDescription = null, tint = AndroidColors.magenta, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                "Nie połączono z komputerem. Wróć do Głównej i połącz.",
                 color = AndroidColors.textSecondary,
                 fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ToggleRow(label: String, value: Boolean, color: Color, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = AndroidColors.textPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = color
            )
        )
    }
}

@Composable
fun legendaryTextFieldColors(color: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = color,
    unfocusedBorderColor = AndroidColors.divider,
    focusedLabelColor = color,
    unfocusedLabelColor = AndroidColors.textMuted,
    focusedTextColor = AndroidColors.textPrimary,
    unfocusedTextColor = AndroidColors.textPrimary,
    cursorColor = color
)
