package com.hinataku.statscounter.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.data.PlayerRepository
import com.hinataku.statscounter.ui.stats.PlayerStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(HomeUiState())
  val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      combine(
        GameRepository.games,
        PlayerRepository.players,
        GameRepository.statsVersion,
      ) { games, players, _ ->
        val allPlayerStats = games.flatMap { GameRepository.getStats(it.id).players }
        val summaries = buildSummaries(players, allPlayerStats)
        val rankings = buildRankings(summaries)
        Triple(games, summaries, rankings)
      }.collect { (games, summaries, rankings) ->
        _uiState.update { it.copy(games = games, playerSummaries = summaries, rankings = rankings) }
      }
    }
  }

  fun onClickAddButton() {
    _uiState.update { it.copy(isAddDialogVisible = true, pendingName = "") }
  }

  fun onChangePendingName(name: String) {
    _uiState.update { it.copy(pendingName = name) }
  }

  fun onConfirmAdd(): Long? {
    val name = _uiState.value.pendingName.trim()
    if (name.isEmpty()) return null
    val gameId = GameRepository.addGame(name)
    _uiState.update { it.copy(isAddDialogVisible = false, pendingName = "") }
    return gameId
  }

  fun onDismissDialog() {
    _uiState.update { it.copy(isAddDialogVisible = false, pendingName = "") }
  }

  fun onLongPressGame(gameId: Long) {
    _uiState.update { it.copy(deletingGameId = gameId) }
  }

  fun onDismissDeleteGameDialog() {
    _uiState.update { it.copy(deletingGameId = null) }
  }

  fun onConfirmDeleteGame() {
    val gameId = _uiState.value.deletingGameId ?: return
    GameRepository.deleteGame(gameId)
    _uiState.update { it.copy(deletingGameId = null) }
  }

  fun onSelectTab(tab: HomeTab) {
    _uiState.update { it.copy(selectedTab = tab) }
  }

  private fun buildSummaries(players: List<Player>, allStats: List<PlayerStats>): List<PlayerSummary> {
    return players.mapNotNull { player ->
      val stats = allStats.filter { it.id == player.id }
      if (stats.isEmpty()) return@mapNotNull null
      PlayerSummary(
        player = player,
        gamesPlayed = stats.size,
        totalPoints = stats.sumOf { it.points },
        total2P = stats.sumOf { it.twoPointMade },
        total3P = stats.sumOf { it.threePointMade },
        totalAssists = stats.sumOf { it.assist },
        totalRebounds = stats.sumOf { it.rebound },
        totalBlocks = stats.sumOf { it.block },
        totalSteals = stats.sumOf { it.steal },
        totalTurnovers = stats.sumOf { it.turnover },
      )
    }
  }

  private fun buildRankings(summaries: List<PlayerSummary>): List<RankingCategory> {
    fun top3(title: String, selector: (PlayerSummary) -> Double): RankingCategory {
      val entries = summaries
        .filter { selector(it) > 0 }
        .sortedWith(compareByDescending(selector).thenBy { it.player.name })
        .take(3)
        .mapIndexed { index, summary ->
          RankingEntry(
            rank = index + 1,
            playerName = summary.player.name,
            value = selector(summary),
          )
        }
      return RankingCategory(title = title, entries = entries)
    }

    return listOf(
      top3("得点", PlayerSummary::avgPoints),
      top3("アシスト", PlayerSummary::avgAssists),
      top3("スティール", PlayerSummary::avgSteals),
      top3("ブロック", PlayerSummary::avgBlocks),
      top3("リバウンド", PlayerSummary::avgRebounds),
      top3("3Pシュート", PlayerSummary::avgThreePoints),
    )
  }
}
