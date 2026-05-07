package com.hinataku.statscounter.ui.stats.blocks

import com.hinataku.statscounter.data.Game
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.ui.stats.StatsUiState
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class StatsTransferPayload(
  val version: Int = 1,
  val players: List<Player>,
  val games: List<Game>,
  val statsMap: Map<String, StatsUiState>,
)

private val statsTransferJson = Json {
  ignoreUnknownKeys = true
  prettyPrint = true
}

fun buildStatsTransferJson(
  players: List<Player>,
  games: List<Game>,
  statsMap: Map<Long, StatsUiState>,
): String {
  return statsTransferJson.encodeToString(
    StatsTransferPayload(
      players = players,
      games = games,
      statsMap = statsMap.mapKeys { it.key.toString() },
    )
  )
}

fun parseStatsTransferJson(text: String): Result<StatsTransferPayload> {
  return runCatching {
    statsTransferJson.decodeFromString<StatsTransferPayload>(text)
  }
}
