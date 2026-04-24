package com.hinataku.statscounter.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PlayerRepository {
  private val json = Json { ignoreUnknownKeys = true }
  private lateinit var appContext: Context

  private val _players = MutableStateFlow<List<Player>>(emptyList())
  val players: StateFlow<List<Player>> = _players.asStateFlow()

  fun init(context: Context) {
    appContext = context.applicationContext
    load()
    if (_players.value.isEmpty()) seedDefaults()
  }

  fun addPlayer(name: String): Player {
    val player = Player(id = System.currentTimeMillis(), name = name)
    _players.update { it + player }
    save()
    return player
  }

  private fun seedDefaults() {
    val names = listOf("おさむ", "みやた", "けいすけ", "ひなたく", "おおてき", "きりやま", "てふぁん", "はやっち")
    _players.value = names.mapIndexed { i, name -> Player(id = (i + 1).toLong(), name = name) }
    save()
  }

  private fun load() {
    val prefs = appContext.getSharedPreferences("players_data", Context.MODE_PRIVATE)
    val str = prefs.getString("data", null) ?: return
    runCatching {
      _players.value = json.decodeFromString(ListSerializer(Player.serializer()), str)
    }
  }

  private fun save() {
    val str = json.encodeToString(ListSerializer(Player.serializer()), _players.value)
    appContext.getSharedPreferences("players_data", Context.MODE_PRIVATE)
      .edit()
      .putString("data", str)
      .apply()
  }
}
