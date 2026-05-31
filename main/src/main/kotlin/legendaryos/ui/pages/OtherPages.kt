package legendaryos.ui.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.core.SystemInfo
import legendaryos.ui.theme.LegendaryColors

// ── Monitor Page ──────────────────────────────────────────────────────────────
@Composable
fun MonitorPage() {
    val info = remember { SystemInfo.collect() }
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Monitor systemu", "Live metryki · procesy · serwisy", Icons.Filled.BarChart, LegendaryColors.pink)

        // Gauge row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(
                Triple("CPU", info.cpuUsage, LegendaryColors.magenta),
                   Triple("RAM", info.ramUsage, LegendaryColors.blue),
                   Triple("Dysk /", info.diskUsage, LegendaryColors.cyan),
                   Triple("Temp.", info.cpuTemp, LegendaryColors.pink),
                   Triple("Swap", 15f, LegendaryColors.violet),
            ).forEach { (label, v, color) ->
                PhoenixMetricCard(Modifier.weight(1f), label, v, if (label == "Temp.") "°C" else "%", color)
            }
        }

        // Procesy
        SectionHeader("Aktywne procesy")
        PhoenixCard {
            Column {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("#", color = LegendaryColors.textMuted, fontSize = 10.sp, modifier = Modifier.width(24.dp))
                    Text("PROCES", color = LegendaryColors.textMuted, fontSize = 10.sp, modifier = Modifier.weight(1f))
                    Text("CPU", color = LegendaryColors.textMuted, fontSize = 10.sp, modifier = Modifier.width(60.dp))
                    Text("RAM", color = LegendaryColors.textMuted, fontSize = 10.sp, modifier = Modifier.width(80.dp))
                    Text("PID", color = LegendaryColors.textMuted, fontSize = 10.sp, modifier = Modifier.width(60.dp))
                }
                HorizontalDivider(color = LegendaryColors.divider)
                listOf(
                    listOf("plasmashell",         "12.4%", "234 MB", "1234"),
                       listOf("kwin_wayland",         "8.1%",  "156 MB", "1235"),
                       listOf("LegendaryOS-App",      "4.2%",  "320 MB", "3421"),
                       listOf("adb",                  "2.0%",  "45 MB",  "5501"),
                       listOf("firefox",              "18.5%", "512 MB", "7823"),
                       listOf("code",                 "6.3%",  "428 MB", "9012"),
                       listOf("systemd",              "0.5%",  "12 MB",  "1"),
                       listOf("pipewire",             "1.2%",  "38 MB",  "1401"),
                ).forEachIndexed { i, (proc, cpu, mem, pid) ->
                    Row(
                        Modifier.fillMaxWidth()
                        .background(if (i % 2 == 0) Color.Transparent else LegendaryColors.surfaceElevated.copy(0.4f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("${i+1}", color = LegendaryColors.textMuted, fontSize = 11.sp, modifier = Modifier.width(24.dp))
                        Text(proc, color = LegendaryColors.textPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                        Text(cpu, color = LegendaryColors.magenta, fontSize = 12.sp, modifier = Modifier.width(60.dp))
                        Text(mem, color = LegendaryColors.blue, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                        Text(pid, color = LegendaryColors.textMuted, fontSize = 11.sp, modifier = Modifier.width(60.dp))
                    }
                }
            }
        }

        // Serwisy systemd
        SectionHeader("Serwisy systemd")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            val services = listOf(
                Triple("NetworkManager", true,  LegendaryColors.success),
                                  Triple("bluetooth",     true,  LegendaryColors.success),
                                  Triple("adb.service",   true,  LegendaryColors.success),
                                  Triple("sshd",          false, LegendaryColors.textMuted),
                                  Triple("cups",          false, LegendaryColors.textMuted),
                                  Triple("firewalld",     true,  LegendaryColors.success),
            )
            Column(Modifier.weight(1f)) {
                services.take(3).forEach { (svc, active, color) ->
                    ServiceRow(svc, active, color)
                }
            }
            Column(Modifier.weight(1f)) {
                services.drop(3).forEach { (svc, active, color) ->
                    ServiceRow(svc, active, color)
                }
            }
        }
    }
}

@Composable
fun ServiceRow(name: String, active: Boolean, color: Color) {
    Row(
        Modifier
        .fillMaxWidth()
        .padding(vertical = 5.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(LegendaryColors.surface)
        .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(color, CircleShape))
        Spacer(Modifier.width(10.dp))
        Text(name, color = LegendaryColors.textPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(if (active) "aktywny" else "nieaktywny", color = color, fontSize = 10.sp)
    }
}

// ── Network Page ─────────────────────────────────────────────────────────────
@Composable
fun NetworkPage() {
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Sieć", "Interfejsy · WiFi · Firewall · ADB WiFi", Icons.Filled.Wifi, LegendaryColors.magenta)

        // Interfejsy
        SectionHeader("Interfejsy sieciowe")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            listOf(
                Triple("eth0",  "192.168.1.100", LegendaryColors.blue),
                   Triple("wlan0", "192.168.1.101", LegendaryColors.magenta),
                   Triple("lo",    "127.0.0.1",     LegendaryColors.textMuted),
            ).forEach { (iface, ip, color) ->
                PhoenixCard(modifier = Modifier.weight(1f), borderColor = color.copy(0.3f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).background(if (ip != "127.0.0.1") LegendaryColors.success else color, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(iface, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(ip, color = LegendaryColors.textPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // ADB WiFi
        SectionHeader("ADB over WiFi")
        var adbIp by remember { mutableStateOf("") }
        PhoenixCard(borderColor = LegendaryColors.blue.copy(0.3f)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = adbIp, onValueChange = { adbIp = it },
                    label = { Text("IP urządzenia Android (np. 192.168.1.50:5555)") },
                                  modifier = Modifier.fillMaxWidth(), singleLine = true,
                                  colors = phxTextFieldColors(LegendaryColors.blue)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallGradientButton("Połącz ADB", Brush.linearGradient(listOf(LegendaryColors.magenta, LegendaryColors.blue)))
                    SmallButton("adb devices", LegendaryColors.cyan, outline = true)
                    SmallButton("adb kill-server", LegendaryColors.error, outline = true)
                }
            }
        }

        // Otwarte porty
        SectionHeader("Otwarte porty")
        PhoenixCard {
            Column {
                listOf(
                    Triple("5037", "adb",       LegendaryColors.blue),
                       Triple("22",   "sshd",      LegendaryColors.success),
                       Triple("631",  "cups",      LegendaryColors.textMuted),
                       Triple("4747", "droidcam",  LegendaryColors.cyan),
                ).forEachIndexed { i, (port, service, color) ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(port, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                             fontFamily = FontFamily.Monospace, modifier = Modifier.width(60.dp))
                        Text(service, color = LegendaryColors.textPrimary, fontSize = 12.sp)
                        Spacer(Modifier.weight(1f))
                        Box(
                            Modifier
                            .background(color.copy(0.15f), RoundedCornerShape(5.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("LISTEN", color = color, fontSize = 9.sp, letterSpacing = 1.sp)
                        }
                    }
                    if (i < 3) HorizontalDivider(color = LegendaryColors.divider)
                }
            }
        }

        // Firewall
        SectionHeader("Firewall (firewalld)")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SmallGradientButton("Włącz firewalld", Brush.linearGradient(listOf(LegendaryColors.success, LegendaryColors.cyan)))
            SmallButton("Status", LegendaryColors.magenta, outline = true)
            SmallButton("Reguły...", LegendaryColors.blue, outline = true)
        }
    }
}

// ── Logs Page ─────────────────────────────────────────────────────────────────
@Composable
fun LogsPage() {
    val scroll = rememberScrollState()
    var filter by remember { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Logi systemowe", "journalctl · bootc · ADB", Icons.AutoMirrored.Filled.Article, LegendaryColors.pink)

        // Filtr
        OutlinedTextField(
            value = filter, onValueChange = { filter = it },
            label = { Text("Filtruj logi...") },
                          modifier = Modifier.fillMaxWidth(), singleLine = true,
                          leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                          colors = phxTextFieldColors(LegendaryColors.pink)
        )

        // Źródła
        SectionHeader("Źródło logów")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("System (journalctl)", "bootc", "ADB", "LegendaryOS App").forEach { src ->
                Box(
                    Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LegendaryColors.surfaceElevated)
                    .border(1.dp, LegendaryColors.divider, RoundedCornerShape(8.dp))
                    .clickable {}
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(src, color = LegendaryColors.textSecondary, fontSize = 11.sp)
                }
            }
        }

        // Log terminal
        val logLines = listOf(
            Triple(LegendaryColors.success,  "INFO",  "systemd[1]: Started NetworkManager.service"),
                              Triple(LegendaryColors.blue,     "INFO",  "adb[5037]: * daemon started successfully"),
                              Triple(LegendaryColors.magenta,  "INFO",  "bootc: Checking for updates at ghcr.io/LegendaryOS"),
                              Triple(LegendaryColors.cyan,     "INFO",  "bootc: Image is up to date: LegendaryOS:latest"),
                              Triple(LegendaryColors.pink,     "INFO",  "LegendaryOS-App: UI initialized (Compose Desktop)"),
                              Triple(LegendaryColors.warning,  "WARN",  "adb[5037]: device unauthorized — check USB Debugging"),
                              Triple(LegendaryColors.blue,     "INFO",  "NetworkManager: wlan0 connected to SSID: HomeNet-5G"),
                              Triple(LegendaryColors.success,  "INFO",  "plasmashell: Plasma 6.1 started on Wayland"),
                              Triple(LegendaryColors.error,    "ERROR", "adb: no device found — retrying in 5s"),
                              Triple(LegendaryColors.cyan,     "INFO",  "LegendaryOS-App: HackerOS SSH bridge ready"),
                              Triple(LegendaryColors.textMuted,"DEBUG", "SystemInfo.collect() took 42ms"),
        )

        Box(
            Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF050608))
            .border(1.dp, LegendaryColors.violet.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
            val innerScroll = rememberScrollState()
            Column(Modifier.fillMaxSize().verticalScroll(innerScroll)) {
                logLines.filter { (_, _, msg) -> filter.isEmpty() || msg.contains(filter, ignoreCase = true) }
                .forEach { (color, level, msg) ->
                    Row(Modifier.padding(vertical = 2.dp)) {
                        Text(
                            level.padEnd(6),
                             color = color,
                             fontSize = 10.sp,
                             fontFamily = FontFamily.Monospace,
                             modifier = Modifier.width(48.dp)
                        )
                        Text(
                            msg,
                             color = Color(0xFFAABBCC),
                             fontSize = 11.sp,
                             fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallGradientButton("Odśwież", Brush.linearGradient(listOf(LegendaryColors.pink, LegendaryColors.violet)))
            SmallButton("Exportuj...", LegendaryColors.cyan, outline = true)
            SmallButton("Wyczyść", LegendaryColors.error, outline = true)
        }
    }
}

// ── System Page ────────────────────────────────────────────────────────────────
@Composable
fun SystemPage() {
    val info = remember { SystemInfo.collect() }
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("System", "LegendaryOS · Fedora 44 · bootc", Icons.Filled.Computer, LegendaryColors.blue)

        SectionHeader("Informacje o systemie")
        PhoenixInfoGrid(listOf(
            "Dystrybucja"   to "LegendaryOS (Fedora 44)",
                               "Mechanizm"     to "bootc + OCI",
                               "Jądro"         to info.kernelVersion.ifEmpty { "6.11.0-300.fc41.x86_64" },
                               "Architektura"  to "x86_64",
                               "DE / WM"       to "KDE Plasma 6 / Wayland",
                               "Obraz OCI"     to "ghcr.io/LegendaryOS/LegendaryOS:latest",
                               "Uptime"        to info.uptime.ifEmpty { "3h 22m" },
                               "Hostname"      to info.hostname.ifEmpty { "legendaryos-pc" },
        ), accentColor = LegendaryColors.blue)

        SectionHeader("bootc — zarządzanie obrazem")
        TerminalBox(
            """$ bootc status
            Current:  ghcr.io/LegendaryOS/LegendaryOS:latest
            Staged:   (brak staged image)
        Rollback: ghcr.io/LegendaryOS/LegendaryOS:1.0.9

        Status: ✓ System aktualny""", color = LegendaryColors.cyan
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallGradientButton("bootc upgrade", Brush.linearGradient(listOf(LegendaryColors.magenta, LegendaryColors.blue)))
            SmallButton("bootc rollback", LegendaryColors.warning, outline = true)
            SmallButton("bootc status", LegendaryColors.textMuted, outline = true)
            SmallButton("bootc switch...", LegendaryColors.cyan, outline = true)
        }

        SectionHeader("Sprzęt")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PhoenixInfoGrid(
                listOf(
                    "CPU"   to "Intel Core i7-12700K",
                    "RAM"   to "16 GB DDR5",
                    "GPU"   to "NVIDIA RTX 3060",
                ),
                modifier = Modifier.weight(1f),
                            accentColor = LegendaryColors.magenta
            )
            PhoenixInfoGrid(
                listOf(
                    "Dysk"  to "Samsung 970 EVO 512GB",
                    "Sieć"  to "Intel I225-V 2.5GbE",
                    "Audio" to "Pipewire 1.0",
                ),
                modifier = Modifier.weight(1f),
                            accentColor = LegendaryColors.cyan
            )
        }
    }
}

// ── Files Page ────────────────────────────────────────────────────────────────
@Composable
fun FilesPage() {
    var currentPath by remember { mutableStateOf("/home/user") }
    var selectedSource by remember { mutableStateOf("LegendaryOS") }
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Menedżer Plików", "Linux ↔ Android · ADB / MTP / SSH", Icons.Filled.Folder, LegendaryColors.cyan)

        // Source tabs
        val sources = listOf("LegendaryOS", "Android (MTP)", "Android (ADB)", "HackerOS (SSH)")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            sources.forEach { src ->
                val sel = src == selectedSource
                Box(
                    Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (sel) LegendaryColors.cyan.copy(0.15f) else LegendaryColors.surface)
                    .border(1.dp, if (sel) LegendaryColors.cyan.copy(0.4f) else LegendaryColors.divider, RoundedCornerShape(8.dp))
                    .clickable { selectedSource = src }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(src, color = if (sel) LegendaryColors.cyan else LegendaryColors.textSecondary, fontSize = 12.sp)
                }
            }
        }

        // Path bar
        OutlinedTextField(
            value = currentPath, onValueChange = { currentPath = it },
            label = { Text("Ścieżka") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                          leadingIcon = { Icon(Icons.Filled.FolderOpen, contentDescription = null) },
                          colors = phxTextFieldColors(LegendaryColors.cyan)
        )

        // File list
        PhoenixCard(borderColor = LegendaryColors.cyan.copy(0.2f)) {
            Column {
                // Toolbar
                Row(
                    Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Nowy folder", "Prześlij", "Pobierz", "Usuń").forEachIndexed { i, label ->
                        val color = listOf(LegendaryColors.cyan, LegendaryColors.magenta, LegendaryColors.blue, LegendaryColors.error)[i]
                        SmallButton(label, color, outline = true)
                    }
                }
                HorizontalDivider(color = LegendaryColors.divider)
                val entries = listOf(
                    Triple("📁", "Documents",       "—"),
                                     Triple("📁", "Downloads",       "—"),
                                     Triple("📁", "Pictures",        "—"),
                                     Triple("📁", "Music",           "—"),
                                     Triple("📁", "Videos",          "—"),
                                     Triple("📄", "README.md",       "2.4 KB"),
                                     Triple("📄", ".bashrc",         "1.1 KB"),
                                     Triple("⚙", "LegendaryOS.conf", "8.7 KB"),
                                     Triple("📄", "legendary.log",   "14 KB"),
                )
                entries.forEachIndexed { i, (ico, name, size) ->
                    Row(
                        Modifier
                        .fillMaxWidth()
                        .background(if (i % 2 == 0) Color.Transparent else LegendaryColors.surfaceElevated.copy(0.3f))
                        .clickable {}
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ico, fontSize = 15.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(name, color = LegendaryColors.textPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Text(size, color = LegendaryColors.textMuted, fontSize = 11.sp)
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = LegendaryColors.textMuted, modifier = Modifier.size(16.dp))
                    }
                    if (i < entries.lastIndex) HorizontalDivider(color = LegendaryColors.divider.copy(0.5f))
                }
            }
        }
    }
}

// ── Terminal Page ─────────────────────────────────────────────────────────────
@Composable
fun TerminalPage() {
    var input by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("LegendaryOS (lokalny)") }
    val history = remember {
        mutableStateListOf(
            "LegendaryOS App — Terminal v1.1",
            "Połączony z: localhost",
            "Wpisz 'help' aby zobaczyć dostępne polecenia.",
            ""
        )
    }
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        PageHeader("Terminal", "Zintegrowana powłoka", Icons.Filled.Terminal, LegendaryColors.violet)

        // Target selector
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("LegendaryOS (lokalny)", "HackerOS (SSH)", "Android (ADB)").forEach { t ->
                val sel = t == target
                Box(
                    Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (sel) LegendaryColors.violet.copy(0.2f) else LegendaryColors.surface)
                    .border(1.dp, if (sel) LegendaryColors.violet.copy(0.5f) else LegendaryColors.divider, RoundedCornerShape(8.dp))
                    .clickable { target = t }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(t, color = if (sel) LegendaryColors.violet else LegendaryColors.textSecondary, fontSize = 12.sp)
                }
            }
        }

        // Output
        Box(
            Modifier
            .weight(1f)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF050608))
            .border(1.dp, LegendaryColors.violet.copy(0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
        ) {
            val scroll = rememberScrollState()
            Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                history.forEach { line ->
                    Text(
                        line,
                         color = when {
                             line.startsWith("$") -> LegendaryColors.magenta
                             line.startsWith("✓") || line.startsWith("●") -> LegendaryColors.success
                             line.startsWith("✗") || line.startsWith("Błąd") -> LegendaryColors.error
                             line.startsWith("Legend") -> LegendaryColors.cyan
                             else -> Color(0xFFAABBCC)
                         },
                         fontSize = 12.sp,
                         fontFamily = FontFamily.Monospace,
                         lineHeight = 18.sp
                    )
                }
            }
        }

        // Input
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("$", color = LegendaryColors.magenta, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
            OutlinedTextField(
                value = input, onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                              placeholder = { Text("Wpisz polecenie...", color = LegendaryColors.textMuted) },
                              singleLine = true,
                              colors = phxTextFieldColors(LegendaryColors.violet)
            )
            SmallGradientButton(
                "Wyślij",
                Brush.linearGradient(listOf(LegendaryColors.magenta, LegendaryColors.violet))
            ) {
                if (input.isNotBlank()) {
                    history.add("$ $input")
                    history.add(executeCommand(input))
                    history.add("")
                    input = ""
                }
            }
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

// ── Updates Page ──────────────────────────────────────────────────────────────
@Composable
fun UpdatesPage() {
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Aktualizacje", "bootc · Flatpak · LegendaryOS App", Icons.Filled.SystemUpdate, LegendaryColors.cyan)

        // Status banner
        Box(
            Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LegendaryColors.success.copy(0.08f))
            .border(1.dp, LegendaryColors.success.copy(0.35f), RoundedCornerShape(14.dp))
            .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = LegendaryColors.success)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("System aktualny", color = LegendaryColors.success, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("Ostatnie sprawdzenie: przed chwilą", color = LegendaryColors.textMuted, fontSize = 11.sp)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SmallGradientButton("Sprawdź aktualizacje", Brush.linearGradient(listOf(LegendaryColors.magenta, LegendaryColors.blue)))
            SmallButton("bootc upgrade", LegendaryColors.cyan, outline = true)
            SmallButton("flatpak update", LegendaryColors.violet, outline = true)
        }

        SectionHeader("Historia aktualizacji")
        TerminalBox("""$ bootc upgrade
        INFO  Fetching: ghcr.io/LegendaryOS/LegendaryOS:latest
        INFO  Image is up to date.
        ✓ No new updates available.

        $ flatpak update
        Looking for updates...
        Nothing to do.

        $ legendary version
        legendary v1.1.0""", color = LegendaryColors.cyan)

        SectionHeader("Zainstalowane pakiety Flatpak")
        PhoenixCard {
            listOf(
                "com.visualstudio.code"             to "1.93.0",
                "org.mozilla.firefox"               to "130.0",
                "info.guardianproject.Scrcpy"       to "2.6",
                "org.kde.kdenlive"                  to "24.08",
                "com.spotify.Client"                to "1.2.42",
            ).forEachIndexed { i, (app, ver) ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(app, color = LegendaryColors.textPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                    Text(ver, color = LegendaryColors.cyan, fontSize = 11.sp)
                }
                if (i < 4) HorizontalDivider(color = LegendaryColors.divider)
            }
        }
    }
}

// ── Settings Page ─────────────────────────────────────────────────────────────
@Composable
fun SettingsPage() {
    var autoStart  by remember { mutableStateOf(true) }
    var notifs     by remember { mutableStateOf(true) }
    var darkMode   by remember { mutableStateOf(true) }
    var adbAuto    by remember { mutableStateOf(false) }
    var smsForward by remember { mutableStateOf(false) }
    var trayIcon   by remember { mutableStateOf(true) }
    val scroll = rememberScrollState()
    Column(
        Modifier.fillMaxSize().verticalScroll(scroll).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        PageHeader("Ustawienia", "Konfiguracja LegendaryOS App", Icons.Filled.Settings, LegendaryColors.textSecondary)

        SectionHeader("Ogólne")
        SettingsGroup {
            SettingToggle("Uruchamiaj przy starcie systemu", autoStart, LegendaryColors.magenta) { autoStart = it }
            HorizontalDivider(color = LegendaryColors.divider)
            SettingToggle("Pokaż ikonę w zasobniku", trayIcon, LegendaryColors.blue) { trayIcon = it }
            HorizontalDivider(color = LegendaryColors.divider)
            SettingToggle("Powiadomienia", notifs, LegendaryColors.cyan) { notifs = it }
            HorizontalDivider(color = LegendaryColors.divider)
            SettingToggle("Ciemny motyw (Phoenix)", darkMode, LegendaryColors.violet) { darkMode = it }
        }

        SectionHeader("Android Bridge")
        SettingsGroup {
            SettingToggle("Auto-połącz ADB przy starcie", adbAuto, LegendaryColors.blue) { adbAuto = it }
            HorizontalDivider(color = LegendaryColors.divider)
            SettingToggle("Przekieruj SMS na komputer", smsForward, LegendaryColors.pink) { smsForward = it }
        }

        SectionHeader("O aplikacji")
        PhoenixInfoGrid(listOf(
            "Wersja"      to "1.1.0",
            "Projekt"     to "LegendaryOS",
            "GUI"         to "Compose Multiplatform (Kotlin 2.0)",
                               "Android"     to "Jetpack Compose",
                               "CLI"         to "Ruby 3.x",
                               "Build"       to "Kotlin 2.0.20 · Compose 1.7.0",
                               "Licencja"    to "GPLv3",
        ), accentColor = LegendaryColors.magenta)
    }
}

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
fun PageHeader(title: String, sub: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier
            .size(46.dp)
            .background(color.copy(0.15f), RoundedCornerShape(13.dp))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Column {
            Text(title, color = LegendaryColors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(sub, color = LegendaryColors.textMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun PhoenixInfoGrid(entries: List<Pair<String, String>>, modifier: Modifier = Modifier, accentColor: Color = LegendaryColors.magenta) {
    Box(
        modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LegendaryColors.surface)
        .border(1.dp, accentColor.copy(0.15f), RoundedCornerShape(14.dp))
        .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            entries.forEachIndexed { i, (k, v) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(k, color = LegendaryColors.textMuted, fontSize = 11.sp, modifier = Modifier.width(140.dp))
                    Text(v, color = LegendaryColors.textPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                if (i < entries.lastIndex) HorizontalDivider(color = LegendaryColors.divider)
            }
        }
    }
}

@Composable
fun TerminalBox(text: String, color: Color = LegendaryColors.cyan) {
    Box(
        Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF050608))
        .border(1.dp, color.copy(0.25f), RoundedCornerShape(10.dp))
        .padding(16.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontFamily = FontFamily.Monospace, lineHeight = 18.sp)
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Box(
        Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LegendaryColors.surface)
        .border(1.dp, LegendaryColors.divider, RoundedCornerShape(14.dp))
        .padding(8.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun ColumnScope.SettingToggle(label: String, value: Boolean, color: Color, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = LegendaryColors.textPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(
            value, onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = color
            )
        )
    }
}

@Composable
fun SmallGradientButton(
    label: String,
    brush: Brush,
    onClick: () -> Unit = {}
) {
    Box(
        Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(brush)
        .clickable { onClick() }
        .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun phxTextFieldColors(color: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = color,
    unfocusedBorderColor = LegendaryColors.divider,
    focusedLabelColor = color,
    unfocusedLabelColor = LegendaryColors.textMuted,
    focusedTextColor = LegendaryColors.textPrimary,
    unfocusedTextColor = LegendaryColors.textPrimary,
    cursorColor = color
)
