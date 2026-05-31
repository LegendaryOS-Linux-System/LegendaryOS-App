package legendaryos.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.ui.pages.*
import legendaryos.ui.theme.LegendaryColors
import legendaryos.ui.theme.LegendaryTheme

// ── Nawigacja ─────────────────────────────────────────────────────────────────
sealed class NavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val group: String = ""
) {
    object Dashboard  : NavItem("Dashboard",    Icons.Filled.Dashboard,     LegendaryColors.magenta, "SYSTEM")
    object System     : NavItem("System",       Icons.Filled.Computer,      LegendaryColors.blue,    "SYSTEM")
    object Updates    : NavItem("Aktualizacje", Icons.Filled.SystemUpdate,  LegendaryColors.cyan,    "SYSTEM")
    object Monitor    : NavItem("Monitor",      Icons.Filled.BarChart,      LegendaryColors.pink,    "SYSTEM")
    object Android    : NavItem("Android",      Icons.Filled.PhoneAndroid,  LegendaryColors.blue,    "MOSTEK")
    object HackerOS   : NavItem("HackerOS",     Icons.Filled.Security,      LegendaryColors.accent,  "MOSTEK")
    object Files      : NavItem("Pliki",        Icons.Filled.Folder,        LegendaryColors.cyan,    "NARZĘDZIA")
    object Terminal   : NavItem("Terminal",     Icons.Filled.Terminal,      LegendaryColors.violet,  "NARZĘDZIA")
    object Network    : NavItem("Sieć",         Icons.Filled.Wifi,          LegendaryColors.magenta, "NARZĘDZIA")
    object Logs       : NavItem("Logi",         Icons.AutoMirrored.Filled.Article,       LegendaryColors.pink,    "NARZĘDZIA")
    object Settings   : NavItem("Ustawienia",   Icons.Filled.Settings,      LegendaryColors.textMuted,"")
}

val navGroups = mapOf(
    "SYSTEM"    to listOf(NavItem.Dashboard, NavItem.System, NavItem.Updates, NavItem.Monitor),
                      "MOSTEK"    to listOf(NavItem.Android, NavItem.HackerOS),
                      "NARZĘDZIA" to listOf(NavItem.Files, NavItem.Terminal, NavItem.Network, NavItem.Logs),
)
val navBottom = listOf(NavItem.Settings)

// ── Root ──────────────────────────────────────────────────────────────────────
@Composable
fun LegendaryOSApp() {
    LegendaryTheme {
        var selectedNav by remember { mutableStateOf<NavItem>(NavItem.Dashboard) }

        Row(
            Modifier
            .fillMaxSize()
            .background(LegendaryColors.background)
        ) {
            Sidebar(selected = selectedNav, onSelect = { selectedNav = it })

            Box(
                Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(LegendaryColors.violet.copy(0.08f), Color.Transparent)
                        ),
                        radius = size.width * 0.6f,
                        center = Offset(size.width * 0.3f, size.height * 0.05f)
                    )
                }
            ) {
                AnimatedContent(
                    targetState = selectedNav,
                    transitionSpec = {
                        (fadeIn(tween(220, easing = EaseOutCubic)) +
                        slideInVertically(tween(220, easing = EaseOutCubic)) { it / 12 }) togetherWith
                        (fadeOut(tween(160)))
                    }
                ) { nav ->
                    when (nav) {
                        NavItem.Dashboard -> DashboardPage()
                        NavItem.System    -> SystemPage()
                        NavItem.Updates   -> UpdatesPage()
                        NavItem.Monitor   -> MonitorPage()
                        NavItem.Android   -> AndroidPage()
                        NavItem.HackerOS  -> HackerOSPage()
                        NavItem.Files     -> FilesPage()
                        NavItem.Terminal  -> TerminalPage()
                        NavItem.Network   -> NetworkPage()
                        NavItem.Logs      -> LogsPage()
                        NavItem.Settings  -> SettingsPage()
                    }
                }
            }
        }
    }
}

// ── Sidebar ───────────────────────────────────────────────────────────────────
@Composable
fun Sidebar(selected: NavItem, onSelect: (NavItem) -> Unit) {
    Column(
        modifier = Modifier
        .width(230.dp)
        .fillMaxHeight()
        .background(LegendaryColors.sidebar)
        .drawBehind {
            // prawa krawędź gradient
            drawLine(
                brush = Brush.verticalGradient(
                    listOf(
                        LegendaryColors.magenta.copy(0.5f),
                           LegendaryColors.violet.copy(0.3f),
                           LegendaryColors.blue.copy(0.5f)
                    )
                ),
                start = Offset(size.width, 0f),
                     end = Offset(size.width, size.height),
                     strokeWidth = 1f
            )
        }
        .padding(vertical = 16.dp),
           verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Logo
            Box(
                Modifier
                .padding(horizontal = 18.dp)
                .padding(bottom = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                        .size(38.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(LegendaryColors.magenta, LegendaryColors.violet, LegendaryColors.blue)
                            ),
                            RoundedCornerShape(11.dp)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("L", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "LegendaryOS",
                             style = androidx.compose.ui.text.TextStyle(
                                 brush = Brush.linearGradient(
                                     listOf(LegendaryColors.magenta, LegendaryColors.blue)
                                 ),
                                 fontSize = 13.sp,
                                 fontWeight = FontWeight.ExtraBold
                             )
                        )
                        Text("App v1.1.0", color = LegendaryColors.textMuted, fontSize = 9.sp, letterSpacing = 1.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = LegendaryColors.divider, modifier = Modifier.padding(horizontal = 14.dp))
            Spacer(Modifier.height(10.dp))

            // Nav grupy
            navGroups.forEach { (groupName, items) ->
                if (groupName.isNotEmpty()) {
                    Text(
                        groupName,
                         color = LegendaryColors.textMuted,
                         fontSize = 9.sp,
                         letterSpacing = 1.5.sp,
                         modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
                items.forEach { item ->
                    SidebarItem(item = item, isSelected = selected == item, onClick = { onSelect(item) })
                }
                Spacer(Modifier.height(4.dp))
            }
        }

        // Dół sidebar
        Column(Modifier.padding(horizontal = 14.dp)) {
            navBottom.forEach { item ->
                SidebarItem(item = item, isSelected = selected == item, onClick = { onSelect(item) })
            }
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = LegendaryColors.divider)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp)) {
                Box(Modifier.size(7.dp).background(LegendaryColors.success, CircleShape))
                Spacer(Modifier.width(7.dp))
                Text("LegendaryOS online", color = LegendaryColors.textMuted, fontSize = 10.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Fedora 44 · bootc",
                 color = LegendaryColors.textMuted,
                 fontSize = 9.sp,
                 modifier = Modifier.padding(horizontal = 14.dp)
            )
        }
    }
}

@Composable
fun SidebarItem(item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bgAlpha by animateFloatAsState(if (isSelected) 1f else 0f, tween(200))
    val textColor by animateColorAsState(
        if (isSelected) item.color else LegendaryColors.textSecondary,
            tween(200)
    )

    Row(
        modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 2.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(item.color.copy(alpha = bgAlpha * 0.12f))
        .then(
            if (isSelected) Modifier.border(
                1.dp,
                Brush.linearGradient(listOf(item.color.copy(0.4f), item.color.copy(0.1f))),
                                            RoundedCornerShape(10.dp)
            ) else Modifier
        )
        .clickable { onClick() }
        .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Marker
        Box(
            Modifier
            .width(3.dp)
            .height(16.dp)
            .alpha(if (isSelected) 1f else 0f)
            .background(
                Brush.verticalGradient(listOf(item.color, item.color.copy(0.4f))),
                        RoundedCornerShape(2.dp)
            )
        )
        Spacer(Modifier.width(if (isSelected) 10.dp else 13.dp))
        Icon(
            item.icon,
             contentDescription = item.label,
             tint = textColor,
             modifier = Modifier.size(17.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            item.label,
             color = textColor,
             fontSize = 13.sp,
             fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
