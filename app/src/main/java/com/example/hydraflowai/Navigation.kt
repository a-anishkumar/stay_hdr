package com.example.hydraflowai

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.hydraflowai.data.repository.WaterRepository
import com.example.hydraflowai.ui.ai.AIInsightsScreen
import com.example.hydraflowai.ui.ai.AIViewModel
import com.example.hydraflowai.ui.analytics.AnalyticsScreen
import com.example.hydraflowai.ui.analytics.AnalyticsViewModel
import com.example.hydraflowai.ui.auth.LoginScreen
import com.example.hydraflowai.ui.dashboard.DashboardScreen
import com.example.hydraflowai.ui.dashboard.DashboardViewModel
import com.example.hydraflowai.ui.onboarding.OnboardingScreen
import com.example.hydraflowai.ui.onboarding.SplashScreen
import com.example.hydraflowai.ui.settings.SettingsScreen
import com.example.hydraflowai.ui.streak.StreakScreen
import com.example.hydraflowai.ui.streak.StreakViewModel

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Splash)
  val app = LocalContext.current.applicationContext as HydraFlowApplication

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Splash> {
          SplashScreen(onTimeout = { backStack.add(Onboarding) })
        }
        entry<Onboarding> {
          val dashboardViewModel: DashboardViewModel = viewModel { DashboardViewModel(app.repository) }
          OnboardingScreen(onComplete = { weight, activity ->
              dashboardViewModel.updateWeight(weight)
              dashboardViewModel.updateActivityLevel(activity)
              backStack.add(Login)
          })
        }
        entry<Login> {
          LoginScreen(onLoginSuccess = { backStack.add(MainFlow) })
        }
        entry<MainFlow> {
          MainFlowScreen(app)
        }
      },
  )
}

@Composable
fun MainFlowScreen(app: HydraFlowApplication) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val dashboardViewModel: DashboardViewModel = viewModel { DashboardViewModel(app.repository) }
    val analyticsViewModel: AnalyticsViewModel = viewModel { AnalyticsViewModel(app.repository) }
    val aiViewModel: AIViewModel = viewModel { AIViewModel(app.repository, app.aiRecommendationEngine, app.weatherService) }
    val streakViewModel: StreakViewModel = viewModel { StreakViewModel(app.repository) }
    
    val repository = app.repository

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                    label = { Text("Stats", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Streaks") },
                    label = { Text("Streak", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "AI") },
                    label = { Text("Coach", fontSize = 11.sp) }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = dashboardViewModel)
                1 -> AnalyticsScreen(viewModel = analyticsViewModel)
                2 -> StreakScreen(viewModel = streakViewModel)
                3 -> AIInsightsScreen(viewModel = aiViewModel)
                4 -> SettingsScreen(repository = repository, dashboardViewModel = dashboardViewModel)
            }
        }
    }
}
