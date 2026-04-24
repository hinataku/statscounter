package com.hinataku.statscounter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.hinataku.statscounter.ui.home.HomeDestination
import com.hinataku.statscounter.ui.home.HomePage
import com.hinataku.statscounter.ui.stats.StatsDestination
import com.hinataku.statscounter.ui.stats.StatsPage

@Composable
fun AppNavigation() {
  val navController = rememberNavController()
  CompositionLocalProvider(LocalNavController provides navController) {
    NavHost(navController = navController, startDestination = HomeDestination) {
      composable<HomeDestination> {
        HomePage()
      }
      composable<StatsDestination> {
        StatsPage(it.toRoute<StatsDestination>().gameId)
      }
    }
  }
}
