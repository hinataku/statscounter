package com.hinataku.statscounter.ui.home

import com.hinataku.statscounter.data.Game
import com.hinataku.statscounter.data.Player

data class HomeUiState(
  val games: List<Game> = emptyList(),
  val isAddDialogVisible: Boolean = false,
  val pendingName: String = "",
  val selectedTab: HomeTab = HomeTab.Games,
  val playerSummaries: List<PlayerSummary> = emptyList(),
  val rankings: List<RankingCategory> = emptyList(),
  val deletingGameId: Long? = null,
  val isSideMenuVisible: Boolean = false,
  val isExportDialogVisible: Boolean = false,
  val exportText: String = "",
  val isImportDialogVisible: Boolean = false,
  val importText: String = "",
  val importErrorText: String? = null,
)

enum class HomeTab { Games, Players, Rankings }

data class PlayerSummary(
  val player: Player,
  val gamesPlayed: Int,
  val totalPoints: Int,
  val total2P: Int,
  val total3P: Int,
  val totalAssists: Int,
  val totalRebounds: Int,
  val totalBlocks: Int,
  val totalSteals: Int,
  val totalTurnovers: Int,
) {
  val avgPoints: Double get() = if (gamesPlayed == 0) 0.0 else totalPoints.toDouble() / gamesPlayed
  val avgThreePoints: Double get() = if (gamesPlayed == 0) 0.0 else total3P.toDouble() / gamesPlayed
  val avgAssists: Double get() = if (gamesPlayed == 0) 0.0 else totalAssists.toDouble() / gamesPlayed
  val avgSteals: Double get() = if (gamesPlayed == 0) 0.0 else totalSteals.toDouble() / gamesPlayed
  val avgBlocks: Double get() = if (gamesPlayed == 0) 0.0 else totalBlocks.toDouble() / gamesPlayed
  val avgRebounds: Double get() = if (gamesPlayed == 0) 0.0 else totalRebounds.toDouble() / gamesPlayed
}

data class RankingCategory(
  val title: String,
  val entries: List<RankingEntry>,
)

data class RankingEntry(
  val rank: Int,
  val playerName: String,
  val value: Double,
)
