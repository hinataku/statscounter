package com.hinataku.statscounter

import androidx.compose.runtime.Composable
import com.hinataku.statscounter.ui.stats.StatsUiState

@Composable
actual fun rememberStatsShareActions(
  screen: AppScreen,
  statsUiState: StatsUiState,
): StatsShareActions = StatsShareActions()
