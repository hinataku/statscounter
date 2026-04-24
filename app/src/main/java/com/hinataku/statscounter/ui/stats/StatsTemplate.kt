package com.hinataku.statscounter.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.ui.stats.blocks.HeaderRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerSelectSheet
import com.hinataku.statscounter.ui.stats.blocks.TotalRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTemplate(
  uiState: StatsUiState,
  allPlayers: List<Player>,
  onBack: () -> Unit,
  onClickAddPlayer: () -> Unit,
  onDismissPlayerSelect: () -> Unit,
  onDismissDeleteDialog: () -> Unit,
  onSelectPlayer: (Player) -> Unit,
  onChangePendingNewPlayerName: (String) -> Unit,
  onConfirmNewPlayer: () -> Unit,
  onLongPressPlayer: (Long) -> Unit,
  onConfirmDelete: () -> Unit,
  onPlus: (id: Long, type: StatType) -> Unit,
  onMinus: (id: Long, type: StatType) -> Unit,
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  val horizontalScroll = rememberScrollState()
  val verticalScroll = rememberScrollState()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      TopAppBar(
        title = { Text("スタッツ集計") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "戻る",
            )
          }
        },
        actions = {
          TextButton(onClick = onClickAddPlayer) { Text("追加") }
        },
        scrollBehavior = scrollBehavior,
      )
    },
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF7F7F7))
        .padding(innerPadding)
    ) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        Column(
          modifier = Modifier
            .verticalScroll(verticalScroll)
            .horizontalScroll(horizontalScroll)
            .padding(8.dp)
        ) {
          HeaderRow()
          uiState.players.forEach { player ->
            PlayerRow(
              player = player,
              onPlus = { onPlus(player.id, it) },
              onMinus = { onMinus(player.id, it) },
              onLongPressName = { onLongPressPlayer(player.id) }
            )
          }
          TotalRow(players = uiState.players)
        }
      }
    }
  }

  if (uiState.isPlayerSelectVisible) {
    PlayerSelectSheet(
      allPlayers = allPlayers,
      alreadyAddedIds = uiState.players.map { it.id }.toSet(),
      pendingNewPlayerName = uiState.pendingNewPlayerName,
      onSelectPlayer = onSelectPlayer,
      onChangePendingName = onChangePendingNewPlayerName,
      onConfirmNewPlayer = onConfirmNewPlayer,
      onDismiss = onDismissPlayerSelect,
    )
  }

  val deletingPlayer = uiState.players.firstOrNull { it.id == uiState.deletingPlayerId }
  if (deletingPlayer != null) {
    AlertDialog(
      onDismissRequest = onDismissDeleteDialog,
      title = { Text("選手を削除") },
      text = { Text("「${deletingPlayer.name}」をこの記録から削除しますか？") },
      confirmButton = {
        TextButton(onClick = onConfirmDelete) { Text("削除") }
      },
      dismissButton = {
        TextButton(onClick = onDismissDeleteDialog) { Text("キャンセル") }
      },
    )
  }
}

@Preview(showBackground = true)
@Composable
private fun StatsTemplatePreview() {
  MaterialTheme {
    StatsTemplate(
      uiState = StatsUiState(
        players = listOf(
          PlayerStats(id = 1, name = "おさむ", twoPointMade = 3, threePointMade = 1, assist = 2),
          PlayerStats(id = 2, name = "みやた"),
        )
      ),
      allPlayers = emptyList(),
      onBack = {},
      onClickAddPlayer = {},
      onDismissPlayerSelect = {},
      onDismissDeleteDialog = {},
      onSelectPlayer = {},
      onChangePendingNewPlayerName = {},
      onConfirmNewPlayer = {},
      onLongPressPlayer = {},
      onConfirmDelete = {},
      onPlus = { _, _ -> },
      onMinus = { _, _ -> },
    )
  }
}
