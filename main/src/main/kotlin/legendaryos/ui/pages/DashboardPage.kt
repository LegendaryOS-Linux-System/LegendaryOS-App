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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.core.SystemInfo
import legendaryos.ui.LegendaryColors

@Composable
fun DashboardPage() {
    val scrollState = rememberScrollState()
    val sysInfo = remember { SystemInfo.collect() }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Column {
            Text(
                "Dashboard",
                color = LegendaryColors.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "LegendaryOS App — centrum kontroli",
                color = LegendaryColors.textMuted,
                fontSize = 13.sp
            )
        }

        // Status cards row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                Modifier.weight(1f),
                title = "LegendaryOS",
                value = "Online",
                sub = "Fedora 44 · bootc",
                icon = Icons.Filled.Computer,
                color = LegendaryColors.success
            )
            StatusCard(
                Modifier.weight(1f),
                title = "Android",
                value = if (sysInfo.androidConnected) "Połączony" else "Brak urządzenia",
                sub = if (sysInfo.androidConnected) sysInfo.androidModel else "Podłącz przez USB / WiFi",
                icon = Icons.Filled.PhoneAndroid,
                color = if (sysInfo.androidConnected) LegendaryColors.accentBlue else LegendaryColors.textMuted
            )
            StatusCard(
                Modifier.weight(1f),
                title = "HackerOS",
                value = "Dostępny",
                sub = "v4.1 · SSH bridge",
                icon = Icons.Filled.Security,
                color = LegendaryColors.accent
            )
        }

        // System metrics
        Text("Zasoby systemowe", color = LegendaryColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(Modifier.weight(1f), "CPU", sysInfo.cpuUsage, "%", LegendaryColors.gold)
            MetricCard(Modifier.weight(1f), "RAM", sysInfo.ramUsage, "%", LegendaryColors.accentBlue)
            MetricCard(Modifier.weight(1f), "Dysk", sysInfo.diskUsage, "%", LegendaryColors.accent)
            MetricCard(Modifier.weight(1f), "Temp.", sysInfo.cpuTemp, "°C", LegendaryColors.warning)
        }

        // Quick actions
        Text("Szybkie akcje", color = LegendaryColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAction(Modifier.weight(1f), "bootc upgrade", Icons.Filled.SystemUpdate, LegendaryColors.gold)
            QuickAction(Modifier.weight(1f), "ADB Shell", Icons.Filled.Terminal, LegendaryColors.accentBlue)
            QuickAction(Modifier.weight(1f), "Mirror ekranu", Icons.Filled.ScreenShare, LegendaryColors.accent)
            QuickAction(Modifier.weight(1f), "SSH HackerOS", Icons.Filled.VpnKey, Color(0xFF7C4DFF))
        }

        // Recent events log
        Text("Ostatnie zdarzenia", color = LegendaryColors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        LogCard()
    }
}

@Composable
fun StatusCard(modifier: Modifier, title: String, value: String, sub: String, icon: ImageVector, color: Color) {
    Card(
        modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = LegendaryColors.textMuted, fontSize = 11.sp)
                Text(value, color = LegendaryColors.textPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(sub, color = LegendaryColors.textMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier, label: String, value: Float, unit: String, color: Color) {
    Card(
        modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = LegendaryColors.textMuted, fontSize = 11.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "${value.toInt()}$unit",
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = value / 100f,
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = LegendaryColors.divider
            )
        }
    }
}

@Composable
fun QuickAction(modifier: Modifier, label: String, icon: ImageVector, color: Color) {
    var hovered by remember { mutableStateOf(false) }
    Card(
        modifier.clickable { /* TODO: execute action */ }
            .pointerHoverIcon(androidx.compose.ui.input.pointer.PointerIcon.Hand),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hovered) color.copy(alpha = 0.15f) else LegendaryColors.surface
        ),
        border = BorderStroke(1.dp, if (hovered) color.copy(alpha = 0.5f) else Color.Transparent)
    ) {
        Column(
            Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(label, color = LegendaryColors.textSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun LogCard() {
    val events = listOf(
        Triple(LegendaryColors.success, "✓", "System uruchomiony pomyślnie"),
        Triple(LegendaryColors.accentBlue, "⚡", "ADB serwer wystartował (port 5037)"),
        Triple(LegendaryColors.gold, "↑", "Sprawdzanie aktualizacji bootc..."),
        Triple(LegendaryColors.textMuted, "·", "Brak nowych aktualizacji"),
        Triple(LegendaryColors.warning, "!", "Urządzenie Android nie wykryte"),
    )

    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            events.forEach { (color, icon, msg) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, color = color, fontSize = 13.sp, modifier = Modifier.width(20.dp))
                    Text(msg, color = LegendaryColors.textSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}
