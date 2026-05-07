package com.hinataku.statscounter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.hinataku.statscounter.ui.home.HomeDestination
import com.hinataku.statscounter.ui.stats.StatsDestination

@Composable
expect fun AppNavigationEffect(navController: NavHostController)

@Composable
fun AppNavigation() {
  val navController = rememberNavController()
  AppNavigationEffect(navController)
  CompositionLocalProvider(LocalNavController provides navController) {
    NavHost(
      navController = navController,
      startDestination = HomeDestination,
    ) {
      HomeDestination.createNode(this)
      StatsDestination.createNode(this)
    }
  }
}
