package com.hinataku.statscounter

import androidx.compose.runtime.Composable
import com.hinataku.statscounter.ui.stats.StatsUiState

data class StatsShareActions(
  val share: (() -> Unit)? = null,
  val save: (() -> Unit)? = null,
  val shareText: ((String) -> Unit)? = null,
)

@Composable
expect fun rememberStatsShareActions(
  screen: AppScreen,
  statsUiState: StatsUiState,
): StatsShareActions
