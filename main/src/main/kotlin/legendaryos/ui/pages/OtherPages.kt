package legendaryos.ui.pages

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
import legendaryos.core.SystemInfo
import legendaryos.ui.LegendaryColors

// ── System Page ───────────────────────────────────────────────────────────────
@Composable
fun SystemPage() {
    val info = remember { SystemInfo.collect() }
    val scrollState = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PageHeader("System", "Informacje o LegendaryOS", Icons.Filled.Computer, LegendaryColors.gold)

        // OS info card
        InfoGrid(listOf(
            "Dystrybucja" to "LegendaryOS (Fedora 44)",
            "Mechanizm" to "bootc + OCI",
            "Jądro" to info.kernelVersion,
            "Architektura" to "x86_64",
            "DE / WM" to "KDE Plasma 6 / Wayland",
            "Wersja obrazu" to "ghcr.io/LegendaryOS/LegendaryOS:latest",
            "Uptime" to info.uptime,
            "Hostname" to info.hostname,
        ))

        // bootc status
        Text("Status bootc", color = LegendaryColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        TerminalBox("""$ bootc status
Current:  ghcr.io/LegendaryOS/LegendaryOS:latest
Staged:   (brak staged image)
Rollback: ghcr.io/LegendaryOS/LegendaryOS:1.0.9

Status: ✓ System aktualny""")

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = LegendaryColors.gold)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("bootc upgrade", color = Color.Black)
            }
            OutlinedButton(onClick = {}) { Text("bootc rollback", color = LegendaryColors.textSecondary) }
            OutlinedButton(onClick = {}) { Text("bootc status", color = LegendaryColors.textSecondary) }
        }
    }
}

// ── Files Page ────────────────────────────────────────────────────────────────
@Composable
fun FilesPage() {
    var currentPath by remember { mutableStateOf("/home") }
    val scrollState = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PageHeader("Menedżer Plików", "Linux ↔ Android · ADB / MTP", Icons.Filled.Folder, LegendaryColors.gold)

        // Source selector
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("LegendaryOS", "Android (MTP)", "Android (ADB)", "HackerOS (SSH)").forEach { src ->
                AssistChip(
                    onClick = {},
                    label = { Text(src, fontSize = 12.sp) }
                )
            }
        }

        // Path bar
        OutlinedTextField(
            value = currentPath, onValueChange = { currentPath = it },
            label = { Text("Ścieżka") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            leadingIcon = { Icon(Icons.Filled.FolderOpen, contentDescription = null) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegendaryColors.gold)
        )

        // File list placeholder
        Card(
            Modifier.fillMaxWidth().height(360.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)
        ) {
            val entries = listOf(
                "📁" to "Documents", "📁" to "Downloads", "📁" to "Pictures",
                "📁" to "Music", "📁" to "Videos", "📄" to "README.md",
                "📄" to ".bashrc", "⚙" to "LegendaryOS.conf"
            )
            Column(Modifier.padding(12.dp).verticalScroll(rememberScrollState())) {
                entries.forEach { (ico, name) ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .clickable {}.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ico, fontSize = 16.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(name, color = LegendaryColors.textPrimary, fontSize = 13.sp)
                    }
                    Divider(color = LegendaryColors.divider, thickness = 0.5.dp)
                }
            }
        }
    }
}

// ── Terminal Page ─────────────────────────────────────────────────────────────
@Composable
fun TerminalPage() {
    var input by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf(
        "LegendaryOS App — Terminal v1.0",
        "Połączony z: localhost",
        "Wpisz 'help' aby zobaczyć dostępne polecenia.",
        ""
    )}
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PageHeader("Terminal", "Zintegrowana powłoka", Icons.Filled.Terminal, LegendaryColors.gold)

        // Terminal output
        val scroll = rememberScrollState()
        Box(
            Modifier.weight(1f).fillMaxWidth()
                .background(Color(0xFF050709), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                history.forEach { line ->
                    Text(
                        line,
                        color = if (line.startsWith("$")) LegendaryColors.gold
                               else if (line.startsWith("●") || line.startsWith("✓")) LegendaryColors.success
                               else Color(0xFFB0BEC5),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Input row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$", color = LegendaryColors.gold, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Wpisz polecenie...", color = LegendaryColors.textMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LegendaryColors.gold,
                    cursorColor = LegendaryColors.gold,
                    focusedTextColor = LegendaryColors.textPrimary,
                    unfocusedTextColor = LegendaryColors.textPrimary
                )
            )
            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        history.add("$ $input")
                        history.add(executeCommand(input))
                        history.add("")
                        input = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LegendaryColors.gold)
            ) { Text("Wyślij", color = Color.Black) }
        }
    }
}

fun executeCommand(cmd: String): String {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("/bin/sh", "-c", cmd))
        process.inputStream.bufferedReader().readText().trim().ifEmpty { "(brak wyjścia)" }
    } catch (e: Exception) {
        "Błąd: ${e.message}"
    }
}

// ── Updates Page ─────────────────────────────────────────────────────────────
@Composable
fun UpdatesPage() {
    val scrollState = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PageHeader("Aktualizacje", "bootc · Flatpak · LegendaryOS App", Icons.Filled.SystemUpdate, LegendaryColors.gold)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LegendaryColors.success.copy(0.08f)),
            border = BorderStroke(1.dp, LegendaryColors.success.copy(0.3f))) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = LegendaryColors.success)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("System aktualny", color = LegendaryColors.success, fontWeight = FontWeight.SemiBold)
                    Text("Ostatnie sprawdzenie: przed chwilą", color = LegendaryColors.textMuted, fontSize = 12.sp)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = LegendaryColors.gold)) {
                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sprawdź aktualizacje", color = Color.Black)
            }
        }
        TerminalBox("$ bootc upgrade\nOK Checking for updates...\n✓ No new updates available.\n\n$ flatpak update\nNothing to do.")
    }
}

// ── Settings Page ─────────────────────────────────────────────────────────────
@Composable
fun SettingsPage() {
    var autoStart by remember { mutableStateOf(true) }
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }
    var adbAutoConnect by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PageHeader("Ustawienia", "Konfiguracja LegendaryOS App", Icons.Filled.Settings, LegendaryColors.gold)
        SettingsGroup("Ogólne") {
            SettingToggle("Uruchamiaj przy starcie systemu", autoStart) { autoStart = it }
            SettingToggle("Powiadomienia", notifications) { notifications = it }
            SettingToggle("Ciemny motyw", darkMode) { darkMode = it }
        }
        SettingsGroup("Android Bridge") {
            SettingToggle("Auto-połącz ADB przy starcie", adbAutoConnect) { adbAutoConnect = it }
        }
        SettingsGroup("O aplikacji") {
            InfoGrid(listOf(
                "Wersja" to "1.0.0",
                "Projekt" to "LegendaryOS",
                "GUI" to "Compose Multiplatform (Kotlin)",
                "Android" to "Jetpack Compose",
                "CLI" to "Ruby 3.x",
                "Licencja" to "GPLv3",
            ))
        }
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────
@Composable
fun PageHeader(title: String, sub: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(44.dp).background(color.copy(0.15f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Column {
            Text(title, color = LegendaryColors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = LegendaryColors.textMuted, fontSize = 13.sp)
        }
    }
}

@Composable
fun InfoGrid(entries: List<Pair<String, String>>) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            entries.forEachIndexed { i, (k, v) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(k, color = LegendaryColors.textMuted, fontSize = 12.sp, modifier = Modifier.width(160.dp))
                    Text(v, color = LegendaryColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                if (i < entries.lastIndex) Divider(color = LegendaryColors.divider, thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TerminalBox(text: String) {
    Box(Modifier.fillMaxWidth().background(Color(0xFF050709), RoundedCornerShape(10.dp)).padding(16.dp)) {
        Text(text, color = Color(0xFF4CAF50), fontSize = 11.sp, fontFamily = FontFamily.Monospace, lineHeight = 18.sp)
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(title, color = LegendaryColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)) {
            Column(Modifier.padding(8.dp), content = content)
        }
    }
}

@Composable
fun ColumnScope.SettingToggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = LegendaryColors.textPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(value, onChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = LegendaryColors.gold))
    }
}
