package com.hinataku.statscounter

import androidx.compose.ui.window.ComposeUIViewController
import com.hinataku.statscounter.data.AppRepositories
import com.hinataku.statscounter.data.KeyValueStore
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

private class IosKeyValueStore : KeyValueStore {
  private val defaults = NSUserDefaults.standardUserDefaults

  override fun getString(key: String): String? = defaults.stringForKey(key)

  override fun putString(key: String, value: String) {
    defaults.setObject(value, forKey = key)
  }
}

fun MainViewController(): UIViewController {
  AppRepositories.initialize(IosKeyValueStore())
  return ComposeUIViewController {
    StatsCounterRoot()
  }
}
