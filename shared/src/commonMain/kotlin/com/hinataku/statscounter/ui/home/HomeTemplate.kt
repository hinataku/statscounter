package com.hinataku.statscounter.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.data.Game
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.ui.home.blocks.GameListItem
import com.hinataku.statscounter.ui.home.blocks.PlayerSummaryCard
import com.hinataku.statscounter.ui.home.blocks.RankingCategoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTemplate(
  uiState: HomeUiState,
  onClickAdd: () -> Unit,
  onChangePendingName: (String) -> Unit,
  onConfirmAdd: () -> Unit,
  onDismissDialog: () -> Unit,
  onClickGame: (Game) -> Unit,
  onLongPressGame: (Game) -> Unit,
  onDismissDeleteGameDialog: () -> Unit,
  onConfirmDeleteGame: () -> Unit,
  onSelectTab: (HomeTab) -> Unit,
) {
  Scaffold(
    topBar = {
      TopAppBar(title = { Text("スタッツ記録") })
    },
    floatingActionButton = {
      if (uiState.selectedTab == HomeTab.Games) {
        FloatingActionButton(onClick = onClickAdd) {
          Text("+")
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
        Tab(
          selected = uiState.selectedTab == HomeTab.Games,
          onClick = { onSelectTab(HomeTab.Games) },
          text = { Text("記録") },
        )
        Tab(
          selected = uiState.selectedTab == HomeTab.Players,
          onClick = { onSelectTab(HomeTab.Players) },
          text = { Text("選手") },
        )
        Tab(
          selected = uiState.selectedTab == HomeTab.Rankings,
          onClick = { onSelectTab(HomeTab.Rankings) },
          text = { Text("ランキング") },
        )
      }

      when (uiState.selectedTab) {
        HomeTab.Games -> GamesTab(
          games = uiState.games,
          onClickGame = onClickGame,
          onLongPressGame = onLongPressGame,
        )
        HomeTab.Players -> PlayersTab(summaries = uiState.playerSummaries)
        HomeTab.Rankings -> RankingsTab(rankings = uiState.rankings)
      }
    }

    if (uiState.isAddDialogVisible) {
      val focusRequester = remember { FocusRequester() }
      val keyboardController = LocalSoftwareKeyboardController.current

      LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
      }

      AlertDialog(
        onDismissRequest = onDismissDialog,
        title = { Text("記録を追加") },
        text = {
          Column {
            Text("名前を入力してください")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = uiState.pendingName,
              onValueChange = onChangePendingName,
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
              placeholder = { Text("例：vs チームA") },
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = { onConfirmAdd() }),
            )
          }
        },
        confirmButton = {
          TextButton(onClick = onConfirmAdd) { Text("追加") }
        },
        dismissButton = {
          TextButton(onClick = onDismissDialog) { Text("キャンセル") }
        },
      )
    }

    val deletingGame = uiState.games.firstOrNull { it.id == uiState.deletingGameId }
    if (deletingGame != null) {
      AlertDialog(
        onDismissRequest = onDismissDeleteGameDialog,
        title = { Text("記録を削除") },
        text = { Text("「${deletingGame.name}」を削除しますか？") },
        confirmButton = {
          TextButton(onClick = onConfirmDeleteGame) { Text("削除") }
        },
        dismissButton = {
          TextButton(onClick = onDismissDeleteGameDialog) { Text("キャンセル") }
        },
      )
    }
  }
}

@Composable
private fun GamesTab(
  games: List<Game>,
  onClickGame: (Game) -> Unit,
  onLongPressGame: (Game) -> Unit,
) {
  if (games.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("+ ボタンで記録を追加", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(games, key = { it.id }) { game ->
        GameListItem(
          game = game,
          onClick = { onClickGame(game) },
          onLongClick = { onLongPressGame(game) },
        )
      }
    }
  }
}

@Composable
private fun PlayersTab(summaries: List<PlayerSummary>) {
  if (summaries.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("記録に選手を追加すると表示されます", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
  } else {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items(summaries, key = { it.player.id }) { summary ->
        PlayerSummaryCard(summary)
      }
    }
  }
}

@Composable
private fun RankingsTab(rankings: List<RankingCategory>) {
  val visibleRankings = rankings.filter { it.entries.isNotEmpty() }
  if (visibleRankings.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Text("記録が追加されるとランキングを表示します", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
    }
  } else {
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      items(visibleRankings, key = { it.title }) { category ->
        RankingCategoryCard(category)
      }
    }
  }
}
