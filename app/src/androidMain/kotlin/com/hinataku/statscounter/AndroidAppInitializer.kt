package com.hinataku.statscounter

import android.content.Context
import com.hinataku.statscounter.data.AppRepositories
import com.hinataku.statscounter.data.KeyValueStore

private class AndroidKeyValueStore(
  context: Context,
) : KeyValueStore {
  private val prefs = context.applicationContext.getSharedPreferences("shared_app_data", Context.MODE_PRIVATE)

  override fun getString(key: String): String? = prefs.getString(key, null)

  override fun putString(key: String, value: String) {
    prefs.edit().putString(key, value).apply()
  }
}

fun initializeSharedApp(context: Context) {
  AppRepositories.initialize(AndroidKeyValueStore(context))
}
