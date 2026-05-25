package legendaryos.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.android.ui.*
import legendaryos.android.bridge.LinuxBridge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LegendaryAndroidTheme {
                LegendaryAndroidApp()
            }
        }
    }
}

// ── Colors ────────────────────────────────────────────────────────────────────
object AndroidColors {
    val background = Color(0xFF0D0F14)
    val surface    = Color(0xFF161920)
    val gold       = Color(0xFFD4A843)
    val accent     = Color(0xFFE8720C)
    val blue       = Color(0xFF3A8EF6)
    val textPrimary   = Color(0xFFE8EAF0)
    val textMuted     = Color(0xFF565C70)
    val success    = Color(0xFF4CAF50)
    val divider    = Color(0xFF2A2D38)
}

// ── Theme ─────────────────────────────────────────────────────────────────────
@Composable
fun LegendaryAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AndroidColors.gold,
            background = AndroidColors.background,
            surface = AndroidColors.surface,
            onPrimary = Color.Black,
            onBackground = AndroidColors.textPrimary,
            onSurface = AndroidColors.textPrimary,
        ),
        content = content
    )
}

// ── Nav destinations ──────────────────────────────────────────────────────────
enum class AndroidNav(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Dashboard("Główna", Icons.Filled.Dashboard),
    Linux("Linux", Icons.Filled.Computer),
    HackerOS("HackerOS", Icons.Filled.Security),
    Files("Pliki", Icons.Filled.Folder),
    Terminal("Terminal", Icons.Filled.Terminal),
    Settings("Ustawienia", Icons.Filled.Settings),
}

// ── App Root ──────────────────────────────────────────────────────────────────
@Composable
fun LegendaryAndroidApp() {
    var currentNav by remember { mutableStateOf(AndroidNav.Dashboard) }
    var linuxConnected by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AndroidColors.background,
        bottomBar = {
            NavigationBar(
                containerColor = AndroidColors.surface,
                tonalElevation = 0.dp
            ) {
                AndroidNav.values().forEach { nav ->
                    NavigationBarItem(
                        selected = currentNav == nav,
                        onClick = { currentNav = nav },
                        icon = { Icon(nav.icon, contentDescription = nav.label, modifier = Modifier.size(20.dp)) },
                        label = { Text(nav.label, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AndroidColors.gold,
                            selectedTextColor = AndroidColors.gold,
                            unselectedIconColor = AndroidColors.textMuted,
                            unselectedTextColor = AndroidColors.textMuted,
                            indicatorColor = AndroidColors.gold.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AnimatedContent(
                targetState = currentNav,
                transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) }
            ) { nav ->
                when (nav) {
                    AndroidNav.Dashboard -> AndroidDashboard(linuxConnected, onConnectClick = { linuxConnected = !linuxConnected })
                    AndroidNav.Linux     -> LinuxControlScreen(linuxConnected)
                    AndroidNav.HackerOS  -> HackerOSScreen()
                    AndroidNav.Files     -> FilesScreen(linuxConnected)
                    AndroidNav.Terminal  -> TerminalScreen(linuxConnected)
                    AndroidNav.Settings  -> SettingsScreen()
                }
            }
        }
    }
}

// ── Dashboard ─────────────────────────────────────────────────────────────────
@Composable
fun AndroidDashboard(connected: Boolean, onConnectClick: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("LegendaryOS App", color = AndroidColors.gold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Android Bridge", color = AndroidColors.textMuted, fontSize = 12.sp)
            }
            Box(
                Modifier.background(
                    if (connected) AndroidColors.success.copy(0.15f) else AndroidColors.accent.copy(0.15f),
                    RoundedCornerShape(20.dp)
                ).padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    if (connected) "● Połączony" else "● Brak połączenia",
                    color = if (connected) AndroidColors.success else AndroidColors.accent,
                    fontSize = 11.sp
                )
            }
        }

        // Connect card
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AndroidColors.surface)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Połącz z LegendaryOS", color = AndroidColors.textPrimary, fontWeight = FontWeight.SemiBold)
                Text(
                    if (connected) "Połączono z: legendaryos-pc (192.168.1.10)"
                    else "Brak połączenia z komputerem Linux",
                    color = AndroidColors.textMuted, fontSize = 12.sp
                )
                Button(
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (connected) AndroidColors.accent else AndroidColors.gold
                    )
                ) {
                    Icon(
                        if (connected) Icons.Filled.LinkOff else Icons.Filled.Link,
                        contentDescription = null, modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(if (connected) "Rozłącz" else "Połącz", color = Color.Black)
                }
            }
        }

        // Quick tiles grid
        Text("Szybkie akcje", color = AndroidColors.textMuted, fontSize = 11.sp)
        val tiles = listOf(
            Triple("Schowek", Icons.Filled.ContentPaste, AndroidColors.gold),
            Triple("Pliki", Icons.Filled.FolderOpen, AndroidColors.blue),
            Triple("Terminal", Icons.Filled.Terminal, AndroidColors.accent),
            Triple("Powiadomienia", Icons.Filled.Notifications, Color(0xFF7C4DFF)),
            Triple("SMS na PC", Icons.Filled.Message, AndroidColors.success),
            Triple("Kamera → Linux", Icons.Filled.CameraAlt, AndroidColors.blue),
        )
        tiles.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { (label, icon, color) ->
                    Card(
                        Modifier.weight(1f).clickable(enabled = connected) {},
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = AndroidColors.surface)
                    ) {
                        Column(
                            Modifier.padding(12.dp).fillMaxWidth().alpha(if (connected) 1f else 0.4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(22.dp))
                            Text(label, color = AndroidColors.textMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

// ── Linux Control ─────────────────────────────────────────────────────────────
@Composable
fun LinuxControlScreen(connected: Boolean) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Sterowanie LegendaryOS", color = AndroidColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (!connected) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AndroidColors.surface)) {
                Text("⚠ Nie połączono z komputerem. Wróć do dashboardu i połącz.",
                    color = AndroidColors.textMuted, fontSize = 13.sp, modifier = Modifier.padding(16.dp))
            }
            return
        }
        val actions = listOf(
            "Zablokuj ekran" to Icons.Filled.Lock,
            "Uśpij komputer" to Icons.Filled.NightShelter,
            "Wyłącz" to Icons.Filled.PowerSettingsNew,
            "Restart" to Icons.Filled.Refresh,
            "bootc upgrade" to Icons.Filled.SystemUpdate,
            "Wymuś aktualizację" to Icons.Filled.Update,
        )
        actions.forEach { (label, icon) ->
            Card(Modifier.fillMaxWidth().clickable { LinuxBridge.sendCommand(label) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = AndroidColors.surface)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = label, tint = AndroidColors.gold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(label, color = AndroidColors.textPrimary, fontSize = 13.sp)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AndroidColors.textMuted)
                }
            }
        }
    }
}

// ── HackerOS Screen ───────────────────────────────────────────────────────────
@Composable
fun HackerOSScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Security, contentDescription = null, tint = AndroidColors.accent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text("HackerOS Bridge", color = AndroidColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Text("Steruj HackerOS z telefonu przez SSH", color = AndroidColors.textMuted, fontSize = 12.sp)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AndroidColors.surface)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("SSH Terminal", color = AndroidColors.accent, fontWeight = FontWeight.SemiBold)
                Text("Zdalny dostęp do powłoki HackerOS", color = AndroidColors.textMuted, fontSize = 12.sp)
                Button(onClick = {}, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AndroidColors.accent)) {
                    Icon(Icons.Filled.Terminal, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Otwórz SSH Shell", color = Color.White)
                }
            }
        }
    }
}

// ── Files Screen ─────────────────────────────────────────────────────────────
@Composable
fun FilesScreen(connected: Boolean) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Transfer Plików", color = AndroidColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Android ↔ LegendaryOS · Android ↔ HackerOS", color = AndroidColors.textMuted, fontSize = 12.sp)
        if (!connected) {
            Text("⚠ Połącz się z komputerem aby przesyłać pliki.", color = AndroidColors.textMuted)
            return
        }
        // Simple mock
        listOf("📁 /home/user/Documents", "📁 /home/user/Pictures", "📄 README.md").forEach {
            Text(it, color = AndroidColors.textPrimary, fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
                    .background(AndroidColors.surface, RoundedCornerShape(8.dp))
                    .padding(12.dp))
        }
    }
}

// ── Terminal Screen ───────────────────────────────────────────────────────────
@Composable
fun TerminalScreen(connected: Boolean) {
    var input by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf("LegendaryOS Mobile Terminal", "") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Terminal", color = AndroidColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFF050709), RoundedCornerShape(10.dp)).padding(12.dp)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                history.forEach {
                    Text(it, color = Color(0xFF4CAF50), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
        if (connected) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input, onValueChange = { input = it },
                    modifier = Modifier.weight(1f), placeholder = { Text("polecenie...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AndroidColors.gold)
                )
                IconButton(onClick = {
                    if (input.isNotBlank()) { history.add("$ $input"); history.add("(wysłano)"); input = "" }
                }) { Icon(Icons.Filled.Send, contentDescription = null, tint = AndroidColors.gold) }
            }
        }
    }
}

// ── Settings Screen ───────────────────────────────────────────────────────────
@Composable
fun SettingsScreen() {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Ustawienia", color = AndroidColors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        listOf("Wersja" to "1.0.0 Android", "Projekt" to "LegendaryOS", "Licencja" to "GPLv3").forEach { (k, v) ->
            Row(Modifier.fillMaxWidth().background(AndroidColors.surface, RoundedCornerShape(8.dp)).padding(14.dp)) {
                Text(k, color = AndroidColors.textMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text(v, color = AndroidColors.textPrimary, fontSize = 12.sp)
            }
        }
    }
}
