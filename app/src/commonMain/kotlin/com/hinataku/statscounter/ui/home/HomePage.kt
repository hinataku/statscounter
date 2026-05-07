package com.hinataku.statscounter.ui.home

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hinataku.statscounter.platform.rememberShareActions
import com.hinataku.statscounter.ui.navigation.LocalNavController
import com.hinataku.statscounter.ui.navigation.navigateTo
import kotlinx.coroutines.launch

@Composable
fun HomePage(
  viewModel: HomeViewModel = viewModel(),
) {
  val snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val navController = LocalNavController.current
  val shareActions = rememberShareActions(gameName = null, statsUiState = null)
  val navigationTo by viewModel.navigationTo.collectAsStateWithLifecycle()

  LaunchedEffect(navigationTo) {
    val destination = navigationTo ?: return@LaunchedEffect
    navController.navigateTo(destination)
    viewModel.completeNavigation()
  }

  HomeTemplate(
    uiState = uiState,
    onClickAdd = viewModel::onClickAddButton,
    onClickMenu = viewModel::onClickMenu,
    onDismissMenu = viewModel::onDismissMenu,
    onClickExport = viewModel::onClickExport,
    onDismissExport = viewModel::onDismissExport,
    onShareExportText = shareActions.shareText,
    onCopyExportText = {
      scope.launch { snackBarHostState.showSnackbar("コピーしました") }
    },
    onClickImport = viewModel::onClickImport,
    onDismissImport = viewModel::onDismissImport,
    onChangeImportText = viewModel::onChangeImportText,
    onConfirmImport = viewModel::onConfirmImport,
    onChangePendingName = viewModel::onChangePendingName,
    onConfirmAdd = viewModel::onConfirmAdd,
    onDismissDialog = viewModel::onDismissDialog,
    onClickGame = { game -> viewModel.onClickGame(game.id) },
    onLongPressGame = { game -> viewModel.onLongPressGame(game.id) },
    onDismissDeleteGameDialog = viewModel::onDismissDeleteGameDialog,
    onConfirmDeleteGame = viewModel::onConfirmDeleteGame,
    onSelectTab = viewModel::onSelectTab,
    snackBarHost = {
      SnackbarHost(
        hostState = snackBarHostState,
      )
    },
  )
}
