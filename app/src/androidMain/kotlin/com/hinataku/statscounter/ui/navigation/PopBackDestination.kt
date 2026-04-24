package com.hinataku.statscounter.ui.navigation

import androidx.navigation.NavController

object PopBackDestination : Destination {
  override fun navigate(navController: NavController) {
    navController.popBackStack()
  }
}
