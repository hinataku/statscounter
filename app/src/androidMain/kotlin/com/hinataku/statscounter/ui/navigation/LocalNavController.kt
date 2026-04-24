package com.hinataku.statscounter.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
  error("NavController not provided")
}
