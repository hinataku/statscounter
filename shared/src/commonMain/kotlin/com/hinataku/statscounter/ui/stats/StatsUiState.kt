package com.hinataku.statscounter.ui.stats

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StatsUiState(
  val players: List<PlayerStats> = emptyList(),
  @Transient val isPlayerSelectVisible: Boolean = false,
  @Transient val pendingNewPlayerName: String = "",
  @Transient val deletingPlayerId: Long? = null,
)

@Serializable
data class PlayerStats(
  val id: Long,
  val name: String,
  val twoPointMade: Int = 0,
  val threePointMade: Int = 0,
  val assist: Int = 0,
  val rebound: Int = 0,
  val block: Int = 0,
  val steal: Int = 0,
  val turnover: Int = 0,
) {
  @Transient
  val points: Int = twoPointMade * 2 + threePointMade * 3
}

@Serializable
enum class StatType {
  TwoPoint, ThreePoint, Assist, Rebound, Block, Steal, Turnover,
}
