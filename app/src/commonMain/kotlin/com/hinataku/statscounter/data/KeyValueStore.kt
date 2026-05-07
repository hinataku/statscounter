package com.hinataku.statscounter.data

interface KeyValueStore {
  fun getString(key: String): String?
  fun putString(key: String, value: String)
}
