package com.hinataku.statscounter.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hinataku.statscounter.ui.stats.StatsShareManager
import com.hinataku.statscounter.ui.stats.StatsUiState
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberShareActions(
  gameName: String?,
  statsUiState: StatsUiState?,
): StatsShareActions {
  return remember(gameName, statsUiState) {
    StatsShareActions(
      shareImage = if (gameName != null && statsUiState != null) {
        { StatsShareManager.shareStatsImage(gameName, statsUiState) }
      } else {
        null
      },
      saveImage = if (gameName != null && statsUiState != null) {
        { StatsShareManager.saveStatsImage(gameName, statsUiState) }
      } else {
        null
      },
      shareText = { text ->
        val activityVC = UIActivityViewController(
          activityItems = listOf(text),
          applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow
          ?.rootViewController
          ?.presentViewController(activityVC, animated = true, completion = null)
        Unit
      },
    )
  }
}
