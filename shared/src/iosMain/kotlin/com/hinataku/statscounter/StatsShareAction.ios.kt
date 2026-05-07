package com.hinataku.statscounter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.ui.stats.StatsShareManager
import com.hinataku.statscounter.ui.stats.StatsUiState
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberStatsShareActions(
  screen: AppScreen,
  statsUiState: StatsUiState,
): StatsShareActions {
  val current = screen as? AppScreen.Stats
  val gameName = current?.let { GameRepository.getGame(it.gameId)?.name } ?: "リスト"

  return remember(screen, statsUiState) {
    StatsShareActions(
      share = current?.let { {
        StatsShareManager.shareStatsImage(gameName, statsUiState)
      }},
      save = current?.let { {
        StatsShareManager.saveStatsImage(gameName, statsUiState)
      }},
      shareText = { text ->
        val activityVC = UIActivityViewController(
          activityItems = listOf(text),
          applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow
          ?.rootViewController
          ?.presentViewController(activityVC, animated = true, completion = null)
      },
    )
  }
}
