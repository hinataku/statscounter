package com.hinataku.statscounter.platform

import androidx.compose.runtime.Composable
import com.hinataku.statscounter.ui.stats.StatsUiState

data class StatsShareActions(
  val shareImage: (() -> Unit)? = null,
  val saveImage: (() -> Unit)? = null,
  val shareText: ((String) -> Unit)? = null,
)

@Composable
expect fun rememberShareActions(
  gameName: String?,
  statsUiState: StatsUiState?,
): StatsShareActions
