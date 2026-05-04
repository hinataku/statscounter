package com.hinataku.statscounter.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object PlayerRepository {
  private val json = Json { ignoreUnknownKeys = true }
  private lateinit var store: KeyValueStore

  private val _players = MutableStateFlow<List<Player>>(emptyList())
  val players: StateFlow<List<Player>> = _players.asStateFlow()

  fun init(store: KeyValueStore) {
    this.store = store
    load()
    if (_players.value.isEmpty()) seedDefaults()
  }

  fun addPlayer(name: String): Player {
    val player = Player(id = currentTimeMillis(), name = name)
    _players.update { it + player }
    save()
    return player
  }

  fun findPlayerByName(name: String): Player? = _players.value.firstOrNull { it.name == name }

  fun getOrCreatePlayer(name: String): Player = findPlayerByName(name) ?: addPlayer(name)

  fun exportPlayers(): List<Player> = _players.value

  fun replaceAll(players: List<Player>) {
    _players.value = players
    save()
  }

  private fun seedDefaults() {
    val names = listOf("おさむ", "みやた", "けいすけ", "ひなたく", "おおてき", "きりやま", "てふぁん", "はやっち")
    _players.value = names.mapIndexed { i, name -> Player(id = (i + 1).toLong(), name = name) }
    save()
  }

  private fun load() {
    val str = store.getString("players_data") ?: return
    runCatching {
      _players.value = json.decodeFromString(ListSerializer(Player.serializer()), str)
    }
  }

  private fun save() {
    val str = json.encodeToString(ListSerializer(Player.serializer()), _players.value)
    store.putString("players_data", str)
  }
}
