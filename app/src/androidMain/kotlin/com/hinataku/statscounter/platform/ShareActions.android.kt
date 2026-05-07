package com.hinataku.statscounter.platform

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.hinataku.statscounter.ui.stats.StatsShareManager
import com.hinataku.statscounter.ui.stats.StatsUiState

@Composable
actual fun rememberShareActions(
  gameName: String?,
  statsUiState: StatsUiState?,
): StatsShareActions {
  val activity = LocalContext.current as? Activity
  return remember(activity, gameName, statsUiState) {
    if (activity == null) {
      StatsShareActions()
    } else {
      StatsShareActions(
        shareImage = if (gameName != null && statsUiState != null) {
          { StatsShareManager.shareStatsImage(activity, gameName, statsUiState) }
        } else {
          null
        },
        saveImage = if (gameName != null && statsUiState != null) {
          { StatsShareManager.saveStatsImage(activity, gameName, statsUiState) }
        } else {
          null
        },
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
