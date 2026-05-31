package legendaryos.ui.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.ui.theme.LegendaryColors   // ← NAPRAWA: bezpośredni import z theme

@Composable
fun HackerOSPage() {
    var host         by remember { mutableStateOf("192.168.1.x") }
    var port         by remember { mutableStateOf("22") }
    var user         by remember { mutableStateOf("hacker") }
    var sshConnected by remember { mutableStateOf(false) }
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
                .size(44.dp)
                .background(LegendaryColors.accent.copy(0.15f), RoundedCornerShape(12.dp))
                .border(1.dp, LegendaryColors.accent.copy(0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Security,
                     contentDescription = null,
                     tint = LegendaryColors.accent,
                     modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    "HackerOS Bridge",
                     color = LegendaryColors.textPrimary,
                     fontSize = 22.sp,
                     fontWeight = FontWeight.Bold
                )
                Text(
                    "Integracja z dystrybucją HackerOS Linux",
                     color = LegendaryColors.textMuted,
                     fontSize = 13.sp
                )
            }
            Spacer(Modifier.weight(1f))
            // Status badge
            Box(
                Modifier
                .background(
                    if (sshConnected) LegendaryColors.success.copy(0.15f)
                        else LegendaryColors.error.copy(0.15f),
                            RoundedCornerShape(20.dp)
                )
                .border(
                    1.dp,
                    if (sshConnected) LegendaryColors.success.copy(0.4f)
                        else LegendaryColors.error.copy(0.4f),
                            RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    if (sshConnected) "● Połączony" else "● Rozłączony",
                        color = if (sshConnected) LegendaryColors.success else LegendaryColors.error,
                     fontSize = 12.sp,
                     fontWeight = FontWeight.Medium
                )
            }
        }

        // SSH Config Card
        Card(
            Modifier.fillMaxWidth(),
             shape = RoundedCornerShape(16.dp),
             colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface),
             border = BorderStroke(1.dp, LegendaryColors.accent.copy(0.2f))
        ) {
            Column(
                Modifier.padding(20.dp),
                   verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Konfiguracja SSH",
                    color = LegendaryColors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = host, onValueChange = { host = it },
                        label = { Text("Host / IP") },
                                      modifier = Modifier.weight(2f),
                                      singleLine = true,
                                      colors = OutlinedTextFieldDefaults.colors(
                                          focusedBorderColor = LegendaryColors.accent,
                                          focusedLabelColor  = LegendaryColors.accent
                                      )
                    )
                    OutlinedTextField(
                        value = port, onValueChange = { port = it },
                        label = { Text("Port") },
                                      modifier = Modifier.weight(1f),
                                      singleLine = true,
                                      colors = OutlinedTextFieldDefaults.colors(
                                          focusedBorderColor = LegendaryColors.accent,
                                          focusedLabelColor  = LegendaryColors.accent
                                      )
                    )
                    OutlinedTextField(
                        value = user, onValueChange = { user = it },
                        label = { Text("Użytkownik") },
                                      modifier = Modifier.weight(1f),
                                      singleLine = true,
                                      colors = OutlinedTextFieldDefaults.colors(
                                          focusedBorderColor = LegendaryColors.accent,
                                          focusedLabelColor  = LegendaryColors.accent
                                      )
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { sshConnected = !sshConnected },
                        colors = ButtonDefaults.buttonColors(containerColor = LegendaryColors.accent)
                    ) {
                        Icon(
                            if (sshConnected) Icons.Filled.LinkOff else Icons.Filled.Link,
                                contentDescription = null,
                             modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (sshConnected) "Rozłącz" else "Połącz SSH")
                    }
                    OutlinedButton(onClick = {}) {
                        Text("Klucz SSH...", color = LegendaryColors.textSecondary)
                    }
                }
            }
        }

        // Feature cards
        Text(
            "Funkcje mostka HackerOS",
            color = LegendaryColors.textSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HackerFeatureCard(Modifier.weight(1f), "Zdalny terminal",    Icons.Filled.Terminal,      LegendaryColors.accent,  sshConnected)
            HackerFeatureCard(Modifier.weight(1f), "Transfer plików",    Icons.Filled.FileCopy,      LegendaryColors.magenta, sshConnected)
            HackerFeatureCard(Modifier.weight(1f), "Wspólny schowek",    Icons.Filled.ContentPaste,  LegendaryColors.blue,    sshConnected)
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            HackerFeatureCard(Modifier.weight(1f), "Port forwarding",    Icons.Filled.Router,         Color(0xFF7C4DFF),       sshConnected)
            HackerFeatureCard(Modifier.weight(1f), "Zdalny ekran (VNC)", Icons.Filled.DesktopWindows, LegendaryColors.success, sshConnected)
            HackerFeatureCard(Modifier.weight(1f), "Narzędzia hacking",  Icons.Filled.BugReport,      LegendaryColors.error,   sshConnected)
        }

        // Info card
        Card(
            Modifier.fillMaxWidth(),
             shape = RoundedCornerShape(14.dp),
             colors = CardDefaults.cardColors(
                 containerColor = LegendaryColors.accent.copy(alpha = 0.08f)
             ),
             border = BorderStroke(1.dp, LegendaryColors.accent.copy(0.3f))
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Filled.Info,
                     contentDescription = null,
                     tint = LegendaryColors.accent,
                     modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "O HackerOS",
                         color = LegendaryColors.accent,
                         fontSize = 12.sp,
                         fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "HackerOS to dystrybucja Linuksa skupiona na cyberbezpieczeństwie i narzędziach " +
                        "dla zaawansowanych użytkowników. LegendaryOS App umożliwia płynną współpracę " +
                        "między oboma systemami przez SSH, VNC oraz mostek plików.",
                         color = LegendaryColors.textSecondary,
                         fontSize = 11.sp,
                         lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun HackerFeatureCard(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean
) {
    Card(
        modifier
        .alpha(if (enabled) 1f else 0.4f)
        .clickable(enabled = enabled) {},
         shape = RoundedCornerShape(14.dp),
         colors = CardDefaults.cardColors(containerColor = LegendaryColors.surface),
         border = BorderStroke(1.dp, color.copy(0.25f))
    ) {
        Column(
            Modifier
            .padding(16.dp)
            .fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier
                .size(40.dp)
                .background(color.copy(0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                     contentDescription = null,
                     tint = color,
                     modifier = Modifier.size(20.dp)
                )
            }
            Text(
                label,
                 color = LegendaryColors.textSecondary,
                 fontSize = 12.sp,
                 fontWeight = FontWeight.Medium
            )
        }
    }
}
