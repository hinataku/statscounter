package com.hinataku.statscounter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.hinataku.statscounter.data.GameRepository
import com.hinataku.statscounter.ui.home.HomeTemplate
import com.hinataku.statscounter.ui.stats.StatsTemplate

@Composable
fun StatsCounterRoot() {
  val scope = rememberCoroutineScope()
  val controller = remember(scope) { StatsCounterController(scope) }
  val screen by controller.screen.collectAsState()
  val homeUiState by controller.homeUiState.collectAsState()
  val statsUiState by controller.statsUiState.collectAsState()
  val allPlayers by controller.allPlayers.collectAsState()
  val shareActions = rememberStatsShareActions(screen, statsUiState)
  val statsTitle = when (val current = screen) {
    is AppScreen.Stats -> GameRepository.getGame(current.gameId)?.name ?: "リスト"
    AppScreen.Home -> "リスト"
  }

  App {
    when (val current = screen) {
      AppScreen.Home -> HomeTemplate(
        uiState = homeUiState,
        onClickAdd = controller::onClickAddButton,
        onClickMenu = controller::onClickMenu,
        onDismissMenu = controller::onDismissMenu,
        onClickExport = controller::onClickExport,
        onDismissExport = controller::onDismissExport,
        onShareExportText = shareActions.shareText,
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
  }
}
