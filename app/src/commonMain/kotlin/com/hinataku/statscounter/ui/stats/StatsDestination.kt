package com.hinataku.statscounter.ui.stats

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.hinataku.statscounter.ui.navigation.SafeArgDestination
import kotlinx.serialization.Serializable

@Serializable
data class StatsDestination(val gameId: Long) : SafeArgDestination() {
  companion object {
    fun createNode(builder: NavGraphBuilder) {
      builder.composable<StatsDestination> { backStackEntry ->
        val destination: StatsDestination = backStackEntry.toRoute()
        StatsPage(gameId = destination.gameId)
      }
    }
  }
}
