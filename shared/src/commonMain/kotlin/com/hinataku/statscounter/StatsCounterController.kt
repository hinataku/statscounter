package com.hinataku.statscounter

import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.data.PlayerRepository
import com.hinataku.statscounter.ui.home.HomeTab
import com.hinataku.statscounter.ui.home.HomeUiState
import com.hinataku.statscounter.ui.home.PlayerSummary
import com.hinataku.statscounter.ui.home.RankingCategory
import com.hinataku.statscounter.ui.home.RankingEntry
import com.hinataku.statscounter.ui.stats.PlayerStats
import com.hinataku.statscounter.ui.stats.StatType
import com.hinataku.statscounter.ui.stats.StatsUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AppScreen {
  data object Home : AppScreen
  data class Stats(val gameId: Long) : AppScreen
}

class StatsCounterController(
  private val scope: CoroutineScope,
) {
  private val _screen = MutableStateFlow<AppScreen>(AppScreen.Home)
  val screen: StateFlow<AppScreen> = _screen.asStateFlow()

  private val _homeUiState = MutableStateFlow(HomeUiState())
  val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

  private val _statsUiState = MutableStateFlow(StatsUiState())
  val statsUiState: StateFlow<StatsUiState> = _statsUiState.asStateFlow()

  val allPlayers = PlayerRepository.players

  init {
    scope.launch {
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
        _homeUiState.update {
          it.copy(games = games, playerSummaries = summaries, rankings = rankings)
        }
      }
    }
  }

  fun openStats(gameId: Long) {
    _statsUiState.value = GameRepository.getStats(gameId)
    _screen.value = AppScreen.Stats(gameId)
  }

  fun closeStats() {
    _screen.value = AppScreen.Home
  }

  fun onClickAddButton() {
    _homeUiState.update { it.copy(isAddDialogVisible = true, pendingName = "") }
  }

  fun onChangePendingName(name: String) {
    _homeUiState.update { it.copy(pendingName = name) }
  }

  fun onConfirmAdd() {
    val name = _homeUiState.value.pendingName.trim()
    if (name.isEmpty()) return
    val gameId = GameRepository.addGame(name)
    _homeUiState.update { it.copy(isAddDialogVisible = false, pendingName = "") }
    openStats(gameId)
  }

  fun onDismissDialog() {
    _homeUiState.update { it.copy(isAddDialogVisible = false, pendingName = "") }
  }

  fun onLongPressGame(gameId: Long) {
    _homeUiState.update { it.copy(deletingGameId = gameId) }
  }

  fun onDismissDeleteGameDialog() {
    _homeUiState.update { it.copy(deletingGameId = null) }
  }

  fun onConfirmDeleteGame() {
    val gameId = _homeUiState.value.deletingGameId ?: return
    GameRepository.deleteGame(gameId)
    _homeUiState.update { it.copy(deletingGameId = null) }
    if (_screen.value == AppScreen.Stats(gameId)) {
      closeStats()
    }
  }

  fun onSelectTab(tab: HomeTab) {
    _homeUiState.update { it.copy(selectedTab = tab) }
  }

  fun onLongPressPlayer(id: Long) {
    _statsUiState.update { it.copy(deletingPlayerId = id) }
  }

  fun onConfirmDelete() {
    val id = _statsUiState.value.deletingPlayerId ?: return
    updateStatsState { state ->
      state.copy(
        players = state.players.filterNot { it.id == id },
        deletingPlayerId = null,
      )
    }
  }

  fun onDismissDeleteDialog() {
    _statsUiState.update { it.copy(deletingPlayerId = null) }
  }

  fun onClickAddPlayer() {
    _statsUiState.update { it.copy(isPlayerSelectVisible = true) }
  }

  fun onDismissPlayerSelect() {
    _statsUiState.update { it.copy(isPlayerSelectVisible = false, pendingNewPlayerName = "") }
  }

  fun onSelectPlayer(player: Player) {
    if (_statsUiState.value.players.any { it.id == player.id }) {
      onDismissPlayerSelect()
      return
    }
    updateStatsState { state ->
      state.copy(
        players = state.players + PlayerStats(id = player.id, name = player.name),
        isPlayerSelectVisible = false,
        pendingNewPlayerName = "",
      )
    }
  }

  fun onChangePendingNewPlayerName(name: String) {
    _statsUiState.update { it.copy(pendingNewPlayerName = name) }
  }

  fun onConfirmNewPlayer() {
    val name = _statsUiState.value.pendingNewPlayerName.trim()
    if (name.isEmpty()) return
    val player = PlayerRepository.addPlayer(name)
    onSelectPlayer(player)
  }

  fun increment(id: Long, type: StatType) {
    val player = _statsUiState.value.players.firstOrNull { it.id == id } ?: return
    updateStat(id, type, player.statValue(type) + 1)
  }

  fun decrement(id: Long, type: StatType) {
    val player = _statsUiState.value.players.firstOrNull { it.id == id } ?: return
    updateStat(id, type, player.statValue(type) - 1)
  }

  private fun updateStat(id: Long, type: StatType, value: Int) {
    val safeValue = value.coerceAtLeast(0)
    updateStatsState { state ->
      state.copy(
        players = state.players.map { player ->
          if (player.id != id) return@map player
          when (type) {
            StatType.TwoPoint -> player.copy(twoPointMade = safeValue)
            StatType.ThreePoint -> player.copy(threePointMade = safeValue)
            StatType.Assist -> player.copy(assist = safeValue)
            StatType.Rebound -> player.copy(rebound = safeValue)
            StatType.Block -> player.copy(block = safeValue)
            StatType.Steal -> player.copy(steal = safeValue)
            StatType.Turnover -> player.copy(turnover = safeValue)
          }
        }
      )
    }
  }

  private fun updateStatsState(transform: (StatsUiState) -> StatsUiState) {
    _statsUiState.update { current ->
      val next = transform(current)
      currentGameId()?.let { GameRepository.updateStats(it, next) }
      next
    }
  }

  private fun currentGameId(): Long? = (screen.value as? AppScreen.Stats)?.gameId

  private fun PlayerStats.statValue(type: StatType) = when (type) {
    StatType.TwoPoint -> twoPointMade
    StatType.ThreePoint -> threePointMade
    StatType.Assist -> assist
    StatType.Rebound -> rebound
    StatType.Block -> block
    StatType.Steal -> steal
    StatType.Turnover -> turnover
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
