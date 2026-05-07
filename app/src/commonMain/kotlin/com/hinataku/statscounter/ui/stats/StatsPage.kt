package com.hinataku.statscounter.ui.stats

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.data.PlayerRepository
import com.hinataku.statscounter.platform.rememberShareActions
import com.hinataku.statscounter.ui.navigation.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun StatsPage(
  gameId: Long,
  snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
  val scope = rememberCoroutineScope()
  val navController = LocalNavController.current
  val viewModel: StatsViewModel = viewModel(
    key = gameId.toString(),
    factory = viewModelFactory { initializer { StatsViewModel(gameId) } },
  )
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val allPlayers by PlayerRepository.players.collectAsStateWithLifecycle()
  val gameName = GameRepository.getGame(gameId)?.name ?: "タイトル"
  val shareActions = rememberShareActions(gameName = gameName, statsUiState = uiState)
  val navigationTo by viewModel.navigationTo.collectAsStateWithLifecycle()

  LaunchedEffect(navigationTo) {
    navigationTo?.let {
      it.navigate(navController)
      viewModel.completeNavigation()
    }
  }

  StatsTemplate(
    title = gameName,
    uiState = uiState,
    allPlayers = allPlayers,
    onBack = viewModel::onBack,
    onClickAddPlayer = viewModel::onClickAddPlayer,
    onClickShare = if (shareActions.shareImage != null || shareActions.saveImage != null) viewModel::onClickShare else null,
    onDismissShareOptions = viewModel::onDismissShareOptions,
    onShareImage = {
      viewModel.onDismissShareOptions()
      shareActions.shareImage?.invoke()
    },
    onSaveImage = {
      viewModel.onDismissShareOptions()
      shareActions.saveImage?.invoke()
      scope.launch { snackbarHostState.showSnackbar("画像を保存しました") }
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
    snackbarHost = {
      SnackbarHost(
        hostState = snackbarHostState,
      )
    },
  )
}
