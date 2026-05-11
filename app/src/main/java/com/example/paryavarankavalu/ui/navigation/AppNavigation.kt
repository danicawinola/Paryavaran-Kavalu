package com.example.paryavarankavalu.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.paryavarankavalu.ui.SubmissionSuccessScreen
import com.example.paryavarankavalu.ui.screens.*

// ── Bottom nav items ──────────────────────────────────────────
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: String        // emoji icon
)

private val bottomNavItems = listOf(
    BottomNavItem("home",      "Map",       "🗺️"),
    BottomNavItem("dashboard", "Dashboard", "📋"),
    BottomNavItem("karma",     "Karma",     "🌿"),
)

// ── Screens that SHOW the bottom nav bar ─────────────────────
private val bottomNavRoutes = setOf("home", "dashboard", "karma")

private val GreenDark    = Color(0xFF1B5E20)
private val GreenPrimary = Color(0xFF2E7D32)
private val BgColor      = Color(0xFFF5EFE6)

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom bar on main screens
    val showBottomBar = currentDestination?.route in bottomNavRoutes

    Scaffold(
        containerColor = BgColor,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor   = GreenPrimary,
                    tonalElevation = androidx.compose.ui.unit.Dp(8f)
                ) {
                    bottomNavItems.forEach { item ->

                        val isSelected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = isSelected,
                            onClick  = {
                                navController.navigate(item.route) {
                                    // Pop up to start so back stack doesn't pile up
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon = {
                                Text(
                                    item.icon,
                                    fontSize = if (isSelected) 22.sp else 20.sp
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    fontSize   = 11.sp,
                                    fontWeight = if (isSelected)
                                        FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected)
                                        GreenPrimary else Color.Gray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = GreenPrimary,
                                unselectedIconColor = Color.Gray,
                                indicatorColor      = Color(0xFFE8F5E9)   // light green highlight
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(navController)
            }

            composable("report") {
                ReportScreen(navController)
            }

            composable("karma") {
                KarmaScreen()
            }

            composable("dashboard") {
                DashboardScreen()
            }

            composable("success") {
                SubmissionSuccessScreen(navController)
            }
        }
    }
}