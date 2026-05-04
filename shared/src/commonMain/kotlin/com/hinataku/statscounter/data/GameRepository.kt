package com.hinataku.statscounter.data

import com.hinataku.statscounter.ui.stats.StatsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class PersistedData(
  val games: List<Game> = emptyList(),
  val statsMap: Map<String, StatsUiState> = emptyMap(),
)

object GameRepository {
  private val json = Json { ignoreUnknownKeys = true }
  private lateinit var store: KeyValueStore

  private val _games = MutableStateFlow<List<Game>>(emptyList())
  val games: StateFlow<List<Game>> = _games.asStateFlow()

  // スタッツが更新されるたびにインクリメントし、ホーム画面の集計を再計算させる
  private val _statsVersion = MutableStateFlow(0)
  val statsVersion: StateFlow<Int> = _statsVersion.asStateFlow()

  private val statsMap = mutableMapOf<Long, StatsUiState>()

  fun init(store: KeyValueStore) {
    this.store = store
    load()
  }

  fun addGame(name: String): Long {
    val id = currentTimeMillis()
    _games.update { listOf(Game(id = id, name = name)) + it }
    statsMap[id] = StatsUiState()
    save()
    return id
  }

  fun deleteGame(gameId: Long) {
    _games.update { games -> games.filterNot { it.id == gameId } }
    statsMap.remove(gameId)
    _statsVersion.update { it + 1 }
    save()
  }

  fun getStats(gameId: Long): StatsUiState = statsMap[gameId] ?: StatsUiState()

  fun getGame(gameId: Long): Game? = _games.value.firstOrNull { it.id == gameId }

  fun exportGames(): List<Game> = _games.value

  fun exportStatsMap(): Map<Long, StatsUiState> = statsMap.toMap()

  fun renameGame(gameId: Long, name: String) {
    _games.update { games ->
      games.map { game -> if (game.id == gameId) game.copy(name = name) else game }
    }
    save()
  }

  fun updateStats(gameId: Long, stats: StatsUiState) {
    statsMap[gameId] = stats
    _statsVersion.update { it + 1 }
    save()
  }

  fun replaceAll(games: List<Game>, importedStatsMap: Map<Long, StatsUiState>) {
    _games.value = games
    statsMap.clear()
    statsMap.putAll(importedStatsMap)
    _statsVersion.update { it + 1 }
    save()
  }

  fun getAllGamesStats(): List<StatsUiState> = statsMap.values.toList()

  private fun load() {
    val str = store.getString("stats_data") ?: return
    runCatching {
      val data = json.decodeFromString<PersistedData>(str)
      _games.value = data.games
      data.statsMap.forEach { (key, stats) -> statsMap[key.toLong()] = stats }
    }
  }

  private fun save() {
    val data = PersistedData(
      games = _games.value,
      statsMap = statsMap.mapKeys { it.key.toString() },
    )
    store.putString("stats_data", json.encodeToString<PersistedData>(data))
  }
}
