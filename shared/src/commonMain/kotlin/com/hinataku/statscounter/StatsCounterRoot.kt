package com.hinataku.statscounter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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

  App {
    when (val current = screen) {
      AppScreen.Home -> HomeTemplate(
        uiState = homeUiState,
        onClickAdd = controller::onClickAddButton,
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
        uiState = statsUiState,
        allPlayers = allPlayers,
        onBack = controller::closeStats,
        onClickAddPlayer = controller::onClickAddPlayer,
        onDismissPlayerSelect = controller::onDismissPlayerSelect,
        onDismissDeleteDialog = controller::onDismissDeleteDialog,
        onSelectPlayer = controller::onSelectPlayer,
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
