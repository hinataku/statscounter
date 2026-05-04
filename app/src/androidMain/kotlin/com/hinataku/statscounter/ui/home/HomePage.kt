package com.hinataku.statscounter.ui.home

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hinataku.statscounter.ui.navigation.LocalNavController
import com.hinataku.statscounter.ui.stats.StatsDestination

@Composable
fun HomePage(viewModel: HomeViewModel = viewModel()) {
  val activity = LocalContext.current as? Activity
  DisposableEffect(activity) {
    val previousOrientation = activity?.requestedOrientation
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    onDispose {
      activity?.requestedOrientation = previousOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
  }

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = LocalNavController.current

  HomeTemplate(
    uiState = uiState,
    onClickAdd = viewModel::onClickAddButton,
    onClickMenu = {},
    onDismissMenu = {},
    onClickExport = {},
    onDismissExport = {},
    onShareExportText = null,
    onClickImport = {},
    onDismissImport = {},
    onChangeImportText = {},
    onConfirmImport = {},
    onChangePendingName = viewModel::onChangePendingName,
    onConfirmAdd = {
      val gameId = viewModel.onConfirmAdd() ?: return@HomeTemplate
      StatsDestination(gameId).navigate(navController)
    },
    onDismissDialog = viewModel::onDismissDialog,
    onClickGame = { game -> StatsDestination(game.id).navigate(navController) },
    onLongPressGame = { game -> viewModel.onLongPressGame(game.id) },
    onDismissDeleteGameDialog = viewModel::onDismissDeleteGameDialog,
    onConfirmDeleteGame = viewModel::onConfirmDeleteGame,
    onSelectTab = viewModel::onSelectTab,
  )
}
