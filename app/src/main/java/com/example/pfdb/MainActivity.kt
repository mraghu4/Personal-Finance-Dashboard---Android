package com.example.pfdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pfdb.ui.FinanceViewModel
import com.example.pfdb.ui.screens.AnalyticsScreen
import com.example.pfdb.ui.screens.AssetScreen
import com.example.pfdb.ui.screens.DashboardScreen
import com.example.pfdb.ui.screens.SettingsScreen
import com.example.pfdb.ui.screens.TimelineScreen
import com.example.pfdb.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Removed enableEdgeToEdge() for "regular window app" feel
        setContent {
            PFDBTheme {
                PFDBApp()
            }
        }
    }
}

@Composable
fun PFDBApp() {
    val viewModel: FinanceViewModel = viewModel()
    var currentDestination by remember { mutableStateOf(AppDestinations.DASHBOARD) }

    Scaffold(
        bottomBar = {
            CustomBottomNav(
                currentDestination = currentDestination,
                onNavigate = { currentDestination = it }
            )
        },
        containerColor = BgBase
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentDestination) {
                AppDestinations.DASHBOARD -> DashboardScreen(viewModel)
                AppDestinations.ASSETS -> AssetScreen(viewModel)
                AppDestinations.TIMELINE -> TimelineScreen(viewModel)
                AppDestinations.SETTINGS -> SettingsScreen(viewModel)
            }
        }
    }
}

@Composable
fun CustomBottomNav(
    currentDestination: AppDestinations,
    onNavigate: (AppDestinations) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        color = BgSurfaceGlass,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            NavItem(
                label = "Home",
                icon = Icons.Default.Home,
                isSelected = currentDestination == AppDestinations.DASHBOARD,
                onClick = { onNavigate(AppDestinations.DASHBOARD) }
            )
            
            // Portfolio
            NavItem(
                label = "Portfolio",
                icon = Icons.Default.AccountBalanceWallet,
                isSelected = currentDestination == AppDestinations.ASSETS,
                onClick = { onNavigate(AppDestinations.ASSETS) }
            )

            // Timeline
            NavItem(
                label = "Timeline",
                icon = Icons.Default.ShowChart,
                isSelected = currentDestination == AppDestinations.TIMELINE,
                onClick = { onNavigate(AppDestinations.TIMELINE) }
            )

            // Settings
            NavItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                isSelected = currentDestination == AppDestinations.SETTINGS,
                onClick = { onNavigate(AppDestinations.SETTINGS) }
            )
        }
    }
}

@Composable
fun RowScope.NavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AccentBlue else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = if (isSelected) AccentBlue else TextSecondary
        )
    }
}

enum class AppDestinations(
    val label: String,
) {
    DASHBOARD("Home"),
    ASSETS("Portfolio"),
    TIMELINE("Timeline"),
    SETTINGS("Settings"),
}
