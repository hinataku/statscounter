package com.hinataku.statscounter.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

interface Destination {
  fun navigate(navController: NavController)
}

/**
 * 型安全ナビゲーションに対応した遷移先クラス。
 * 実装クラスは @Serializable である必要がある。
 */
@Serializable
abstract class SafeArgDestination : Destination {
  override fun navigate(navController: NavController) {
    navController.navigate(this)
  }
}
