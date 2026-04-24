package com.hinataku.statscounter

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun App(content: @Composable () -> Unit) {
  MaterialTheme {
    content()
  }
}
