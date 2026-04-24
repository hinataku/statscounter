package com.hinataku.statscounter.data

import kotlinx.serialization.Serializable

@Serializable
data class Game(
  val id: Long,
  val name: String,
)
