package com.hinataku.statscounter.ui.stats

import androidx.lifecycle.ViewModel
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.data.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StatsViewModel(private val gameId: Long) : ViewModel() {
  private val _uiState = MutableStateFlow(
    GameRepository.getStats(gameId).let { stats ->
      stats.copy(
        isPlayerSelectVisible = stats.players.isEmpty(),
        isShareOptionsVisible = false,
        pendingSelectedPlayerIds = stats.players.map { it.id }.toSet(),
        pendingPlayerStats = stats.players.associateBy { it.id },
      )
    }
  )
  val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

  private fun updateState(transform: (StatsUiState) -> StatsUiState) {
    _uiState.update { current ->
      val next = transform(current)
      GameRepository.updateStats(gameId, next)
      next
    }
  }

  fun onLongPressPlayer(id: Long) {
    _uiState.update { it.copy(deletingPlayerId = id) }
  }

  fun onConfirmDelete() {
    val id = _uiState.value.deletingPlayerId ?: return
    updateState { state ->
      state.copy(
        players = state.players.filterNot { it.id == id },
        deletingPlayerId = null,
      )
    }
  }

  fun onDismissDeleteDialog() {
    _uiState.update { it.copy(deletingPlayerId = null) }
  }

  fun onClickShare() {
    _uiState.update { it.copy(isShareOptionsVisible = true) }
  }

  fun onDismissShareOptions() {
    _uiState.update { it.copy(isShareOptionsVisible = false) }
  }

  fun onClickAddPlayer() {
    _uiState.update {
      it.copy(
        isPlayerSelectVisible = true,
        pendingSelectedPlayerIds = it.players.map { player -> player.id }.toSet(),
        pendingPlayerStats = it.players.associateBy { player -> player.id },
      )
    }
  }

  fun onDismissPlayerSelect() {
    _uiState.update {
      it.copy(
        isPlayerSelectVisible = false,
        pendingNewPlayerName = "",
        pendingSelectedPlayerIds = emptySet(),
        pendingPlayerStats = emptyMap(),
      )
    }
  }

  fun onTogglePlayerSelection(player: Player) {
    val allPlayers = PlayerRepository.players.value
    updateState { state ->
      val nextSelected = if (player.id in state.pendingSelectedPlayerIds) {
        state.pendingSelectedPlayerIds - player.id
      } else {
        state.pendingSelectedPlayerIds + player.id
      }
      val nextDrafts = state.pendingPlayerStats.toMutableMap()
      if (player.id !in nextDrafts) {
        nextDrafts[player.id] = PlayerStats(id = player.id, name = player.name)
      }
      state.copy(
        players = allPlayers.mapNotNull { candidate ->
          if (candidate.id !in nextSelected) return@mapNotNull null
          nextDrafts[candidate.id] ?: PlayerStats(id = candidate.id, name = candidate.name)
        },
        pendingSelectedPlayerIds = nextSelected,
        pendingPlayerStats = nextDrafts,
      )
    }
  }

  fun onConfirmSelectedPlayers() {
    onDismissPlayerSelect()
  }

  fun onChangePendingNewPlayerName(name: String) {
    _uiState.update { it.copy(pendingNewPlayerName = name) }
  }

  fun onConfirmNewPlayer() {
    val name = _uiState.value.pendingNewPlayerName.trim()
    if (name.isEmpty()) return
    val player = PlayerRepository.addPlayer(name)
    val allPlayers = PlayerRepository.players.value
    updateState { state ->
      val nextDrafts = state.pendingPlayerStats + (player.id to PlayerStats(id = player.id, name = player.name))
      val nextSelected = state.pendingSelectedPlayerIds + player.id
      state.copy(
        players = allPlayers.mapNotNull { candidate ->
          if (candidate.id !in nextSelected) return@mapNotNull null
          nextDrafts[candidate.id] ?: PlayerStats(id = candidate.id, name = candidate.name)
        },
        pendingNewPlayerName = "",
        pendingSelectedPlayerIds = nextSelected,
        pendingPlayerStats = nextDrafts,
      )
    }
  }

  fun updateStat(id: Long, type: StatType, value: Int) {
    val safeValue = value.coerceAtLeast(0)
    updateState { state ->
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

  fun increment(id: Long, type: StatType) {
    val player = _uiState.value.players.firstOrNull { it.id == id } ?: return
    val current = player.statValue(type)
    updateStat(id, type, current + 1)
  }

  fun decrement(id: Long, type: StatType) {
    val player = _uiState.value.players.firstOrNull { it.id == id } ?: return
    val current = player.statValue(type)
    updateStat(id, type, current - 1)
  }

  fun clearAllStats() {
    updateState { state ->
      state.copy(
        players = state.players.map {
          it.copy(twoPointMade = 0, threePointMade = 0, assist = 0, rebound = 0, block = 0, steal = 0, turnover = 0)
        }
      )
    }
  }

  private fun PlayerStats.statValue(type: StatType) = when (type) {
    StatType.TwoPoint -> twoPointMade
    StatType.ThreePoint -> threePointMade
    StatType.Assist -> assist
    StatType.Rebound -> rebound
    StatType.Block -> block
    StatType.Steal -> steal
    StatType.Turnover -> turnover
  }
}
