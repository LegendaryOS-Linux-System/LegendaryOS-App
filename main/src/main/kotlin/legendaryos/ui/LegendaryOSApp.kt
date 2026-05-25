package legendaryos.ui

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import legendaryos.ui.pages.*
import legendaryos.ui.theme.LegendaryTheme

// ── Navigation Items ─────────────────────────────────────────────────────────
sealed class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard   : NavItem("Dashboard",   Icons.Filled.Dashboard)
    object Android     : NavItem("Android",     Icons.Filled.PhoneAndroid)
    object System      : NavItem("System",      Icons.Filled.Computer)
    object Files       : NavItem("Pliki",       Icons.Filled.Folder)
    object Terminal    : NavItem("Terminal",    Icons.Filled.Terminal)
    object Updates     : NavItem("Aktualizacje",Icons.Filled.SystemUpdate)
    object HackerOS    : NavItem("HackerOS",    Icons.Filled.Security)
    object Settings    : NavItem("Ustawienia",  Icons.Filled.Settings)
}

val navItems = listOf(
    NavItem.Dashboard,
    NavItem.Android,
    NavItem.System,
    NavItem.Files,
    NavItem.Terminal,
    NavItem.Updates,
    NavItem.HackerOS,
    NavItem.Settings,
)

// ── Root App ─────────────────────────────────────────────────────────────────
@Composable
fun LegendaryOSApp() {
    LegendaryTheme {
        var selectedNav by remember { mutableStateOf<NavItem>(NavItem.Dashboard) }

        Row(Modifier.fillMaxSize().background(LegendaryColors.background)) {
            // ── Sidebar ──────────────────────────────────────────────────────
            Sidebar(
                selected = selectedNav,
                onSelect = { selectedNav = it }
            )

            // ── Content ───────────────────────────────────────────────────────
            Box(Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = selectedNav,
                    transitionSpec = {
                        (fadeIn(tween(220)) + slideInHorizontally { it / 6 }) togetherWith
                        (fadeOut(tween(180)) + slideOutHorizontally { -it / 6 })
                    }
                ) { nav ->
                    when (nav) {
                        NavItem.Dashboard -> DashboardPage()
                        NavItem.Android   -> AndroidPage()
                        NavItem.System    -> SystemPage()
                        NavItem.Files     -> FilesPage()
                        NavItem.Terminal  -> TerminalPage()
                        NavItem.Updates   -> UpdatesPage()
                        NavItem.HackerOS  -> HackerOSPage()
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
            .width(220.dp)
            .fillMaxHeight()
            .background(LegendaryColors.sidebar)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Logo
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(36.dp)
                        .background(
                            Brush.linearGradient(listOf(LegendaryColors.gold, LegendaryColors.accent)),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("L", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "LegendaryOS",
                        color = LegendaryColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text("App v1.0", color = LegendaryColors.textMuted, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            Divider(color = LegendaryColors.divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            // Nav items
            navItems.forEach { item ->
                SidebarItem(
                    item = item,
                    isSelected = selected == item,
                    onClick = { onSelect(item) }
                )
            }
        }

        // Footer
        Column(Modifier.padding(horizontal = 16.dp)) {
            Divider(color = LegendaryColors.divider, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text("System online", color = LegendaryColors.textMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun SidebarItem(item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
    val bg by animateColorAsState(
        if (isSelected) LegendaryColors.sidebarSelected else Color.Transparent,
        tween(200)
    )
    val textColor by animateColorAsState(
        if (isSelected) LegendaryColors.gold else LegendaryColors.textSecondary,
        tween(200)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Box(
                Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(LegendaryColors.gold, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.width(10.dp))
        } else {
            Spacer(Modifier.width(13.dp))
        }

        Icon(item.icon, contentDescription = item.label, tint = textColor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(item.label, color = textColor, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
