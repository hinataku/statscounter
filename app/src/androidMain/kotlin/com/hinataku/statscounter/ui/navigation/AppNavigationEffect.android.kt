package com.hinataku.statscounter.ui.navigation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.hinataku.statscounter.ui.stats.StatsDestination

@Composable
actual fun AppNavigationEffect(navController: NavHostController) {
  val activity = LocalContext.current as? Activity
  val view = LocalView.current
  val backStackEntry = navController.currentBackStackEntryAsState().value
  val isStatsScreen = backStackEntry?.let { entry ->
    runCatching { entry.toRoute<StatsDestination>() }.isSuccess
  } == true

  // TODO: requestedOrientation causes aspect-ratio letterboxing on Android 12+ (compatibility mode).
  //       configChanges="orientation|screenSize" in the manifest exists solely to suppress the
  //       activity restart triggered by this call. Remove both together when a proper replacement
  //       is in place (e.g. forcing landscape via Compose layout rotation or WindowManager hints).
  DisposableEffect(activity, isStatsScreen) {
    val targetOrientation = if (isStatsScreen) {
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    } else {
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    if (activity?.requestedOrientation != targetOrientation) {
      activity?.requestedOrientation = targetOrientation
    }
    onDispose {}
  }

  DisposableEffect(view, isStatsScreen) {
    view.keepScreenOn = isStatsScreen
    onDispose {
      view.keepScreenOn = false
    }
  }
}
