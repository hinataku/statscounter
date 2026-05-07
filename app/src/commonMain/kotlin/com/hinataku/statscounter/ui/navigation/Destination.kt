package com.hinataku.statscounter.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

interface Destination

@Serializable
abstract class SafeArgDestination : Destination

fun NavController.navigateTo(destination: Destination) {
  when (destination) {
    is SafeArgDestination -> navigate(destination)
    PopBackDestination -> popBackStack()
    else -> error("Unknown destination: $destination")
  }
}
