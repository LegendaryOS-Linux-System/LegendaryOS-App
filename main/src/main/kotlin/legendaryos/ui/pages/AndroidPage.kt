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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.bridge.AdbBridge
import legendaryos.ui.theme.LegendaryColors   // ← NAPRAWA: bezpośredni import z theme

@Composable
fun AndroidPage() {
    var connectedDevice by remember { mutableStateOf<String?>(null) }
    var connectionMode  by remember { mutableStateOf("USB") }
    var wifiIp          by remember { mutableStateOf("") }
    var adbOutput       by remember { mutableStateOf("Gotowy do połączenia...") }
    val scrollState = rememberScrollState()

    Column(
        Modifier.fillMaxSize().verticalScroll(scrollState).padding(28.dp),
           verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                .size(46.dp)
                .background(LegendaryColors.blue.copy(0.15f), RoundedCornerShape(13.dp))
                .border(1.dp, LegendaryColors.blue.copy(0.3f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PhoneAndroid,
                     contentDescription = null,
                     tint = LegendaryColors.blue,
                     modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    "Android Bridge",
                     color = LegendaryColors.textPrimary,
                     fontSize = 22.sp,
                     fontWeight = FontWeight.Bold
                )
                Text(
                    "Steruj telefonem z poziomu LegendaryOS",
                     color = LegendaryColors.textMuted,
                     fontSize = 13.sp
                )
            }
        }

        // Connection panel
        Card(
            Modifier.fillMaxWidth(),
             shape = RoundedCornerShape(16.dp),
             colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface)
        ) {
            Column(
                Modifier.padding(20.dp),
                   verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Połączenie",
                     color = LegendaryColors.textSecondary,
                     fontSize = 12.sp,
                     fontWeight = FontWeight.Medium
                )

                // Mode toggle
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("USB", "WiFi (ADB Wireless)", "Bluetooth").forEach { mode ->
                        FilterChip(
                            selected = connectionMode == mode,
                            onClick = { connectionMode = mode },
                            label = { Text(mode, fontSize = 12.sp) },
                                   colors = FilterChipDefaults.filterChipColors(
                                       selectedContainerColor = LegendaryColors.blue.copy(alpha = 0.2f),
                                                                                selectedLabelColor = LegendaryColors.blue
                                   )
                        )
                    }
                }

                if (connectionMode == "WiFi (ADB Wireless)") {
                    OutlinedTextField(
                        value = wifiIp,
                        onValueChange = { wifiIp = it },
                        label = { Text("IP urządzenia (np. 192.168.1.100:5555)") },
                                      modifier = Modifier.fillMaxWidth(),
                                      colors = OutlinedTextFieldDefaults.colors(
                                          focusedBorderColor = LegendaryColors.blue,
                                          focusedLabelColor  = LegendaryColors.blue
                                      ),
                                      singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val result = AdbBridge.connect(connectionMode, wifiIp)
                            adbOutput = result
                            connectedDevice = if (result.contains("device")) "Android Device" else null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LegendaryColors.blue)
                    ) {
                        Icon(
                            Icons.Filled.Link,
                             contentDescription = null,
                             modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Połącz")
                    }
                    OutlinedButton(onClick = { adbOutput = AdbBridge.listDevices() }) {
                        Text("Lista urządzeń", color = LegendaryColors.textSecondary)
                    }
                }

                // ADB output
                Box(
                    Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF050608))
                    .padding(12.dp)
                ) {
                    Text(
                        adbOutput,
                         color = LegendaryColors.success,
                         fontSize = 11.sp,
                         fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Feature cards
        Text(
            "Funkcje mostka Android",
            color = LegendaryColors.textSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        val features = listOf(
            AndroidFeature(
                "Lustrzany ekran",
                "Scrcpy — wyświetl ekran telefonu na pulpicie",
                Icons.AutoMirrored.Filled.ScreenShare,
                LegendaryColors.blue
            ) { AdbBridge.launchScrcpy() },
                              AndroidFeature(
                                  "Schowek",
                                  "Synchronizuj schowek między Linux ↔ Android",
                                  Icons.Filled.ContentPaste,
                                  LegendaryColors.magenta
                              ) { AdbBridge.syncClipboard() },
                              AndroidFeature(
                                  "Transfer plików",
                                  "Przeglądaj i kopiuj pliki przez MTP/ADB",
                                  Icons.Filled.FolderOpen,
                                  LegendaryColors.cyan
                              ) { AdbBridge.openFileTransfer() },
                              AndroidFeature(
                                  "Powiadomienia",
                                  "Wyświetlaj powiadomienia telefonu na pulpicie",
                                  Icons.Filled.Notifications,
                                  LegendaryColors.violet
                              ) { AdbBridge.enableNotificationBridge() },
                              AndroidFeature(
                                  "Kamera",
                                  "Użyj aparatu telefonu jako kamerki do Linux",
                                  Icons.Filled.CameraAlt,
                                  LegendaryColors.success
                              ) { AdbBridge.enableWebcam() },
                              AndroidFeature(
                                  "SMS",
                                  "Wysyłaj i odbieraj SMS z komputera",
                                  Icons.AutoMirrored.Filled.Message,
                                  LegendaryColors.info
                              ) { AdbBridge.openSmsManager() },
                              AndroidFeature(
                                  "ADB Shell",
                                  "Interaktywna powłoka na urządzeniu Android",
                                  Icons.Filled.Terminal,
                                  LegendaryColors.pink
                              ) { AdbBridge.openAdbShell() },
                              AndroidFeature(
                                  "Hotspot USB",
                                  "Udostępnij internet z telefonu przez USB",
                                  Icons.Filled.Wifi,
                                  LegendaryColors.accent
                              ) { AdbBridge.enableUsbTethering() },
        )

        features.chunked(4).forEach { row ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { feature ->
                    AndroidFeatureCard(
                        Modifier.weight(1f),
                                       feature,
                                       enabled = connectedDevice != null
                    )
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

data class AndroidFeature(
    val title: String,
    val desc: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val action: () -> Unit
)

@Composable
fun AndroidFeatureCard(modifier: Modifier, feature: AndroidFeature, enabled: Boolean) {
    Card(
        modifier
        .alpha(if (enabled) 1f else 0.4f)
        .clickable(enabled = enabled) { feature.action() },
         shape = RoundedCornerShape(14.dp),
         colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface),
         border = BorderStroke(1.dp, feature.color.copy(0.25f))
    ) {
        Column(
            Modifier.padding(16.dp),
               verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                .size(38.dp)
                .background(feature.color.copy(0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    feature.icon,
                     contentDescription = null,
                     tint = feature.color,
                     modifier = Modifier.size(20.dp)
                )
            }
            Text(
                feature.title,
                 color = LegendaryColors.textPrimary,
                 fontSize = 13.sp,
                 fontWeight = FontWeight.SemiBold
            )
            Text(
                feature.desc,
                 color = LegendaryColors.textMuted,
                 fontSize = 10.sp,
                 lineHeight = 14.sp
            )
        }
    }
}
