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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.core.SystemInfo
import legendaryos.ui.theme.LegendaryColors

@Composable
fun DashboardPage() {
    val scrollState = rememberScrollState()
    val sysInfo = remember { SystemInfo.collect() }

    Column(
        Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Header z gradientem
        Column {
            androidx.compose.ui.text.buildAnnotatedString { }
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Dashboard",
                         style = androidx.compose.ui.text.TextStyle(
                             brush = Brush.linearGradient(
                                 listOf(LegendaryColors.magenta, LegendaryColors.blue)
                             ),
                             fontSize = 28.sp,
                             fontWeight = FontWeight.ExtraBold
                         )
                    )
                    Text(
                        "LegendaryOS App — centrum sterowania",
                         color = LegendaryColors.textMuted,
                         fontSize = 13.sp
                    )
                }
                // Uptime badge
                Box(
                    Modifier
                    .background(
                        Brush.linearGradient(listOf(LegendaryColors.violet.copy(0.2f), LegendaryColors.blue.copy(0.1f))),
                                RoundedCornerShape(10.dp)
                    )
                    .border(1.dp, LegendaryColors.violet.copy(0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(7.dp).background(LegendaryColors.success, CircleShape))
                        Spacer(Modifier.width(7.dp))
                        Text(sysInfo.uptime.ifEmpty { "up 3h 22m" }, color = LegendaryColors.textSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // Status cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            GlowStatusCard(
                Modifier.weight(1f),
                           title = "LegendaryOS",
                           value = "Online",
                           sub = "Fedora 44 · bootc",
                           icon = Icons.Filled.Computer,
                           color = LegendaryColors.magenta
            )
            GlowStatusCard(
                Modifier.weight(1f),
                           title = "Android",
                           value = if (sysInfo.androidConnected) "Połączony" else "Brak urządzenia",
                           sub = if (sysInfo.androidConnected) sysInfo.androidModel else "USB / WiFi",
                           icon = Icons.Filled.PhoneAndroid,
                           color = if (sysInfo.androidConnected) LegendaryColors.blue else LegendaryColors.textMuted
            )
            GlowStatusCard(
                Modifier.weight(1f),
                           title = "HackerOS",
                           value = "Gotowy",
                           sub = "SSH bridge · v4.1",
                           icon = Icons.Filled.Security,
                           color = LegendaryColors.accent
            )
            GlowStatusCard(
                Modifier.weight(1f),
                           title = "bootc",
                           value = "Aktualny",
                           sub = ":latest · OCI",
                           icon = Icons.Filled.CloudDone,
                           color = LegendaryColors.cyan
            )
        }

        // Metryki systemowe
        SectionHeader("Zasoby systemowe")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            PhoenixMetricCard(Modifier.weight(1f), "CPU",   sysInfo.cpuUsage,  "%",  LegendaryColors.magenta)
            PhoenixMetricCard(Modifier.weight(1f), "RAM",   sysInfo.ramUsage,  "%",  LegendaryColors.blue)
            PhoenixMetricCard(Modifier.weight(1f), "Dysk",  sysInfo.diskUsage, "%",  LegendaryColors.cyan)
            PhoenixMetricCard(Modifier.weight(1f), "Temp.", sysInfo.cpuTemp,   "°C", LegendaryColors.pink)
        }

        // Szybkie akcje 2x4
        SectionHeader("Szybkie akcje")
        val actions = listOf(
            ActionTile("bootc upgrade",   Icons.Filled.SystemUpdate, LegendaryColors.magenta),
                             ActionTile("ADB Shell",       Icons.Filled.Terminal,     LegendaryColors.blue),
                             ActionTile("Mirror ekranu",   Icons.AutoMirrored.Filled.ScreenShare,  LegendaryColors.cyan),
                             ActionTile("SSH HackerOS",    Icons.Filled.VpnKey,       LegendaryColors.accent),
                             ActionTile("Schowek sync",    Icons.Filled.ContentPaste, LegendaryColors.violet),
                             ActionTile("Flatpak update",  Icons.Filled.GetApp,       LegendaryColors.pink),
                             ActionTile("Zasilanie",       Icons.Filled.PowerSettingsNew, LegendaryColors.error),
                             ActionTile("Nmap scan",       Icons.Filled.NetworkCheck,  LegendaryColors.success),
        )
        actions.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { tile ->
                    PhoenixActionCard(Modifier.weight(1f), tile)
                }
            }
        }

        // Dwa panele obok siebie — bootc status + log
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader("bootc Image")
                BootcStatusCard()
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                SectionHeader("Ostatnie zdarzenia")
                EventLogCard()
            }
        }
    }
}

data class ActionTile(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun GlowStatusCard(modifier: Modifier, title: String, value: String, sub: String, icon: ImageVector, color: Color) {
    Box(
        modifier
        .clip(RoundedCornerShape(16.dp))
        .background(LegendaryColors.surface)
        .border(
            1.dp,
            Brush.linearGradient(listOf(color.copy(0.5f), color.copy(0.1f))),
                RoundedCornerShape(16.dp)
        )
        .drawBehind {
            drawCircle(
                brush = Brush.radialGradient(listOf(color.copy(0.08f), Color.Transparent)),
                       radius = size.width,
                       center = Offset(0f, 0f)
            )
        }
        .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                .size(40.dp)
                .background(color.copy(0.15f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = LegendaryColors.textMuted, fontSize = 10.sp, letterSpacing = 0.5.sp)
                Text(value, color = LegendaryColors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(sub, color = LegendaryColors.textMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun PhoenixMetricCard(modifier: Modifier, label: String, value: Float, unit: String, color: Color) {
    Box(
        modifier
        .clip(RoundedCornerShape(14.dp))
        .background(LegendaryColors.surface)
        .border(1.dp, color.copy(0.2f), RoundedCornerShape(14.dp))
        .padding(16.dp)
    ) {
        Column {
            Text(label, color = LegendaryColors.textMuted, fontSize = 11.sp)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "${value.toInt()}",
                     style = androidx.compose.ui.text.TextStyle(
                         brush = Brush.linearGradient(listOf(color, color.copy(0.6f))),
                                                                fontSize = 26.sp,
                                                                fontWeight = FontWeight.ExtraBold
                     )
                )
                Text(unit, color = color.copy(0.6f), fontSize = 13.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(LegendaryColors.divider)
            ) {
                Box(
                    Modifier
                    .fillMaxWidth(value / 100f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(color, color.copy(0.5f))))
                )
            }
        }
    }
}

@Composable
fun PhoenixActionCard(modifier: Modifier, tile: ActionTile) {
    Box(
        modifier
        .clip(RoundedCornerShape(12.dp))
        .background(LegendaryColors.surface)
        .border(1.dp, tile.color.copy(0.25f), RoundedCornerShape(12.dp))
        .clickable {}
        .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                .size(36.dp)
                .background(tile.color.copy(0.15f), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tile.icon, contentDescription = tile.label, tint = tile.color, modifier = Modifier.size(19.dp))
            }
            Text(tile.label, color = LegendaryColors.textSecondary, fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
fun BootcStatusCard() {
    PhoenixCard(borderColor = LegendaryColors.cyan.copy(0.3f)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CloudDone, contentDescription = null, tint = LegendaryColors.cyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("System aktualny", color = LegendaryColors.success, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            listOf(
                "Current"  to "LegendaryOS:latest",
                "Staged"   to "(brak)",
                   "Rollback" to "LegendaryOS:1.0.9",
            ).forEach { (k, v) ->
                Row {
                    Text(k.padEnd(12), color = LegendaryColors.textMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(v, color = LegendaryColors.cyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
            HorizontalDivider(color = LegendaryColors.divider)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallButton("Upgrade", LegendaryColors.magenta)
                SmallButton("Rollback", LegendaryColors.textMuted, outline = true)
            }
        }
    }
}

@Composable
fun EventLogCard() {
    PhoenixCard(borderColor = LegendaryColors.violet.copy(0.3f)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                Triple(LegendaryColors.success,  "✓", "System uruchomiony pomyślnie"),
                   Triple(LegendaryColors.blue,     "⚡", "ADB serwer aktywny (5037)"),
                   Triple(LegendaryColors.magenta,  "↑", "Sprawdzanie bootc updates"),
                   Triple(LegendaryColors.textMuted,"·", "Brak nowych aktualizacji"),
                   Triple(LegendaryColors.warning,  "!", "Android nie wykryty przez ADB"),
                   Triple(LegendaryColors.cyan,     "i", "HackerOS SSH bridge gotowy"),
            ).forEach { (color, icon, msg) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, color = color, fontSize = 12.sp, modifier = Modifier.width(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(msg, color = LegendaryColors.textSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text.uppercase(),
         color = LegendaryColors.textMuted,
         fontSize = 10.sp,
         letterSpacing = 1.5.sp,
         fontWeight = FontWeight.Medium
    )
}

@Composable
fun PhoenixCard(
    modifier: Modifier = Modifier,
    borderColor: Color = LegendaryColors.divider,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .background(LegendaryColors.surface)
        .border(1.dp, borderColor, RoundedCornerShape(14.dp))
        .padding(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun SmallButton(label: String, color: Color, outline: Boolean = false) {
    Box(
        Modifier
        .clip(RoundedCornerShape(7.dp))
        .then(
            if (outline) Modifier
                .border(1.dp, color.copy(0.5f), RoundedCornerShape(7.dp))
                else Modifier.background(color.copy(0.2f))
        )
        .clickable {}
        .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(label, color = if (outline) color else color, fontSize = 11.sp)
    }
}
