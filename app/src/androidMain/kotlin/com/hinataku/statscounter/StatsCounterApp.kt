package com.hinataku.statscounter

import android.app.Application
import com.hinataku.statscounter.initializeSharedApp

class StatsCounterApp : Application() {
  override fun onCreate() {
    super.onCreate()
    initializeSharedApp(this)
  }
}
