package com.hinataku.statscounter.data

object AppRepositories {
  private var initialized = false

  fun initialize(store: KeyValueStore) {
    if (initialized) return
    PlayerRepository.init(store)
    GameRepository.init(store)
    initialized = true
  }
}
