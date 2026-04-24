package com.hinataku.statscounter.ui.home

import androidx.navigation.NavController
import com.hinataku.statscounter.ui.navigation.Destination
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : Destination {
  override fun navigate(navController: NavController) = navController.navigate(this)
}
