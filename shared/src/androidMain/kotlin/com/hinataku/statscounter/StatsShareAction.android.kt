package com.hinataku.statscounter

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.ui.stats.StatsShareManager
import com.hinataku.statscounter.ui.stats.StatsUiState

@Composable
actual fun rememberStatsShareActions(
  screen: AppScreen,
  statsUiState: StatsUiState,
): StatsShareActions {
  val activity = LocalContext.current as? Activity
  return remember(activity, screen, statsUiState) {
    if (activity == null) {
      StatsShareActions()
    } else {
      val current = screen as? AppScreen.Stats
      val gameName = current?.let { GameRepository.getGame(it.gameId)?.name } ?: "リスト"
      StatsShareActions(
        share = current?.let { { StatsShareManager.shareStatsImage(activity, gameName, statsUiState) } },
        save = current?.let { { StatsShareManager.saveStatsImage(activity, gameName, statsUiState) } },
        shareText = { text ->
          val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_TEXT, text)
          }
          activity.startActivity(Intent.createChooser(sendIntent, "データを共有"))
        },
      )
    }
  }
}
