package com.hinataku.statscounter

import androidx.compose.ui.window.ComposeUIViewController
import com.hinataku.statscounter.data.AppRepositories
import com.hinataku.statscounter.data.KeyValueStore
import com.hinataku.statscounter.ui.navigation.AppNavigation
import platform.Foundation.NSDate
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
  println("[MainViewController] initialize start ${NSDate()}")
  AppRepositories.initialize(IosKeyValueStore())
  return ComposeUIViewController {
    println("[MainViewController] compose content start ${NSDate()}")
    App {
      AppNavigation()
    }
    println("[MainViewController] compose content end ${NSDate()}")
  }
}
