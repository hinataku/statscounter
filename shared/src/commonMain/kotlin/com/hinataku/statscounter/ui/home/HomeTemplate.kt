package com.hinataku.statscounter.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
  onClickMenu: () -> Unit,
  onDismissMenu: () -> Unit,
  onClickExport: () -> Unit,
  onDismissExport: () -> Unit,
  onShareExportText: ((String) -> Unit)?,
  onCopyExportText: () -> Unit,
  onClickImport: () -> Unit,
  onDismissImport: () -> Unit,
  onChangeImportText: (String) -> Unit,
  onConfirmImport: () -> Unit,
  onChangePendingName: (String) -> Unit,
  onConfirmAdd: () -> Unit,
  onDismissDialog: () -> Unit,
  onClickGame: (Game) -> Unit,
  onLongPressGame: (Game) -> Unit,
  onDismissDeleteGameDialog: () -> Unit,
  onConfirmDeleteGame: () -> Unit,
  onSelectTab: (HomeTab) -> Unit,
) {
  val clipboardManager = LocalClipboardManager.current

  Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("スタッツ記録") },
          actions = {
            TextButton(onClick = onClickMenu) { Text("⋮") }
          },
        )
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
    }

    AnimatedVisibility(
      visible = uiState.isSideMenuVisible,
      enter = fadeIn(),
      exit = fadeOut(),
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x66000000))
          .clickable(onClick = onDismissMenu),
      )
    }

    AnimatedVisibility(
      visible = uiState.isSideMenuVisible,
      enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
      exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
      modifier = Modifier.align(Alignment.TopEnd),
    ) {
      androidx.compose.material3.Card(
        modifier = Modifier
          .fillMaxHeight()
          .width(220.dp),
      ) {
        Column(modifier = Modifier.padding(top = 72.dp, start = 12.dp, end = 12.dp)) {
          TextButton(onClick = onClickExport, modifier = Modifier.fillMaxWidth()) {
            Text("エクスポート")
          }
          TextButton(onClick = onClickImport, modifier = Modifier.fillMaxWidth()) {
            Text("インポート")
          }
        }
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

    if (uiState.isExportDialogVisible) {
      AlertDialog(
        onDismissRequest = onDismissExport,
        title = { Text("エクスポート") },
        text = {
          Column {
            Text("タップでJSONをコピー")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .border(1.dp, Color(0xFFD1D5DB), MaterialTheme.shapes.medium)
                .clickable {
                  clipboardManager.setText(AnnotatedString(uiState.exportText))
                  onCopyExportText()
                }
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            ) {
              Text(
                text = uiState.exportText,
                style = MaterialTheme.typography.bodySmall,
              )
            }
          }
        },
        confirmButton = {
          if (onShareExportText != null) {
            TextButton(onClick = { onShareExportText(uiState.exportText) }) { Text("共有") }
          }
        },
        dismissButton = {
          TextButton(onClick = onDismissExport) { Text("閉じる") }
        },
      )
    }

    if (uiState.isImportDialogVisible) {
      AlertDialog(
        onDismissRequest = onDismissImport,
        title = { Text("インポート") },
        text = {
          Column {
            OutlinedTextField(
              value = uiState.importText,
              onValueChange = onChangeImportText,
              minLines = 12,
              supportingText = {
                uiState.importErrorText?.let { Text(it, color = Color(0xFFB91C1C)) }
              },
            )
          }
        },
        confirmButton = {
          TextButton(onClick = onConfirmImport) { Text("反映") }
        },
        dismissButton = {
          TextButton(onClick = onDismissImport) { Text("閉じる") }
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
