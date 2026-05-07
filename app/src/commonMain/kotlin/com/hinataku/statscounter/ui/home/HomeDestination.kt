package com.hinataku.statscounter.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.hinataku.statscounter.ui.navigation.SafeArgDestination
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : SafeArgDestination() {
  fun createNode(builder: NavGraphBuilder) {
    builder.composable<HomeDestination> {
      HomePage()
    }
  }
}
