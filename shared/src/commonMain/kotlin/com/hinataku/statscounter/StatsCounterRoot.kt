package com.hinataku.statscounter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.ui.home.HomeTemplate
import com.hinataku.statscounter.ui.stats.StatsTemplate
import kotlinx.coroutines.launch

@Composable
fun StatsCounterRoot() {
  val scope = rememberCoroutineScope()
  val controller = remember(scope) { StatsCounterController(scope) }
  val screen by controller.screen.collectAsState()
  val homeUiState by controller.homeUiState.collectAsState()
  val statsUiState by controller.statsUiState.collectAsState()
  val allPlayers by controller.allPlayers.collectAsState()
  val shareActions = rememberStatsShareActions(screen, statsUiState)
  val snackbarHostState = remember { SnackbarHostState() }
  val statsTitle = when (val current = screen) {
    is AppScreen.Stats -> GameRepository.getGame(current.gameId)?.name ?: "リスト"
    AppScreen.Home -> "リスト"
  }

  App {
    Box(Modifier.fillMaxSize()) {
      when (val current = screen) {
        AppScreen.Home -> HomeTemplate(
          uiState = homeUiState,
          onClickAdd = controller::onClickAddButton,
          onClickMenu = controller::onClickMenu,
          onDismissMenu = controller::onDismissMenu,
          onClickExport = controller::onClickExport,
          onDismissExport = controller::onDismissExport,
          onShareExportText = shareActions.shareText,
          onCopyExportText = { scope.launch { snackbarHostState.showSnackbar("コピーしました") } },
          onClickImport = controller::onClickImport,
          onDismissImport = controller::onDismissImport,
          onChangeImportText = controller::onChangeImportText,
          onConfirmImport = controller::onConfirmImport,
          onChangePendingName = controller::onChangePendingName,
          onConfirmAdd = controller::onConfirmAdd,
          onDismissDialog = controller::onDismissDialog,
          onClickGame = { controller.openStats(it.id) },
          onLongPressGame = { controller.onLongPressGame(it.id) },
          onDismissDeleteGameDialog = controller::onDismissDeleteGameDialog,
          onConfirmDeleteGame = controller::onConfirmDeleteGame,
          onSelectTab = controller::onSelectTab,
        )

        is AppScreen.Stats -> StatsTemplate(
          title = statsTitle,
          uiState = statsUiState,
          allPlayers = allPlayers,
          onBack = controller::closeStats,
          onClickAddPlayer = controller::onClickAddPlayer,
          onClickShare = if (shareActions.share != null || shareActions.save != null) controller::onClickShare else null,
          onDismissShareOptions = controller::onDismissShareOptions,
          onShareImage = {
            controller.onDismissShareOptions()
            shareActions.share?.invoke()
          },
          onSaveImage = {
            controller.onDismissShareOptions()
            shareActions.save?.invoke()
            scope.launch { snackbarHostState.showSnackbar("画像を保存しました") }
          },
          onDismissPlayerSelect = controller::onDismissPlayerSelect,
          onDismissDeleteDialog = controller::onDismissDeleteDialog,
          onTogglePlayerSelection = controller::onTogglePlayerSelection,
          onConfirmSelectedPlayers = controller::onConfirmSelectedPlayers,
          onChangePendingNewPlayerName = controller::onChangePendingNewPlayerName,
          onConfirmNewPlayer = controller::onConfirmNewPlayer,
          onLongPressPlayer = controller::onLongPressPlayer,
          onConfirmDelete = controller::onConfirmDelete,
          onPlus = controller::increment,
          onMinus = controller::decrement,
        )
      }
      SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter),
      )
    }
  }
}
