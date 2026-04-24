package com.hinataku.statscounter.ui.stats

import androidx.navigation.NavController
import com.hinataku.statscounter.ui.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
data class StatsDestination(val gameId: Long) : Destination {
  override fun navigate(navController: NavController) = navController.navigate(this)
}
