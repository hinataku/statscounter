package com.hinataku.statscounter.ui.navigation

import androidx.navigation.NavController

interface Destination {
  fun navigate(navController: NavController)
}
