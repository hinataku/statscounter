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
import com.hinataku.statscounter.data.GameRepository
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
  val gameName = GameRepository.getGame(gameId)?.name ?: "タイトル"

  StatsTemplate(
    title = gameName,
    uiState = uiState,
    allPlayers = allPlayers,
    onBack = { navController.popBackStack() },
    onClickAddPlayer = viewModel::onClickAddPlayer,
    onClickShare = viewModel::onClickShare,
    onDismissShareOptions = viewModel::onDismissShareOptions,
    onShareImage = {
      viewModel.onDismissShareOptions()
      activity?.let { StatsShareManager.shareStatsImage(it, gameName, uiState) }
    },
    onSaveImage = {
      viewModel.onDismissShareOptions()
      activity?.let { StatsShareManager.saveStatsImage(it, gameName, uiState) }
    },
    onDismissPlayerSelect = viewModel::onDismissPlayerSelect,
    onDismissDeleteDialog = viewModel::onDismissDeleteDialog,
    onTogglePlayerSelection = viewModel::onTogglePlayerSelection,
    onConfirmSelectedPlayers = viewModel::onConfirmSelectedPlayers,
    onChangePendingNewPlayerName = viewModel::onChangePendingNewPlayerName,
    onConfirmNewPlayer = viewModel::onConfirmNewPlayer,
    onLongPressPlayer = viewModel::onLongPressPlayer,
    onConfirmDelete = viewModel::onConfirmDelete,
    onPlus = viewModel::increment,
    onMinus = viewModel::decrement,
  )
}
