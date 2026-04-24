package com.hinataku.statscounter.ui.stats

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hinataku.statscounter.data.PlayerRepository
import com.hinataku.statscounter.ui.navigation.LocalNavController

@Composable
fun StatsPage(gameId: Long) {
  val activity = LocalContext.current as? Activity
  val view = LocalView.current
  val navController = LocalNavController.current
  DisposableEffect(activity, view) {
    val previousOrientation = activity?.requestedOrientation
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    view.keepScreenOn = true
    onDispose {
      activity?.requestedOrientation = previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
      view.keepScreenOn = false
    }
  }

  val viewModel: StatsViewModel = viewModel(
    key = gameId.toString(),
    factory = viewModelFactory { initializer { StatsViewModel(gameId) } },
  )
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val allPlayers by PlayerRepository.players.collectAsStateWithLifecycle()

  StatsTemplate(
    uiState = uiState,
    allPlayers = allPlayers,
    onBack = { navController.popBackStack() },
    onClickAddPlayer = viewModel::onClickAddPlayer,
    onDismissPlayerSelect = viewModel::onDismissPlayerSelect,
    onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
    onSelectPlayer = viewModel::onSelectPlayer,
    onChangePendingNewPlayerName = viewModel::onChangePendingNewPlayerName,
    onConfirmNewPlayer = viewModel::onConfirmNewPlayer,
    onLongPressPlayer = viewModel::onLongPressPlayer,
    onConfirmDelete = viewModel::onConfirmDelete,
    onPlus = viewModel::increment,
    onMinus = viewModel::decrement,
  )
}
