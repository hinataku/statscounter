package com.hinataku.statscounter

import android.app.Application
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.data.PlayerRepository

class StatsCounterApp : Application() {
  override fun onCreate() {
    super.onCreate()
    PlayerRepository.init(this)
    GameRepository.init(this)
  }
}
