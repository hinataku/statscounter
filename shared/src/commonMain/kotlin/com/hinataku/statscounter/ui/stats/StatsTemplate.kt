package com.hinataku.statscounter.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.ui.stats.blocks.HeaderRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerSelectSheet
import com.hinataku.statscounter.ui.stats.blocks.TotalRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTemplate(
  title: String,
  uiState: StatsUiState,
  allPlayers: List<Player>,
  onBack: () -> Unit,
  onClickAddPlayer: () -> Unit,
  onClickShare: (() -> Unit)? = null,
  onDismissShareOptions: () -> Unit = {},
  onShareImage: () -> Unit = {},
  onSaveImage: () -> Unit = {},
  onDismissPlayerSelect: () -> Unit,
  onDismissDeleteDialog: () -> Unit,
  onTogglePlayerSelection: (Player) -> Unit,
  onConfirmSelectedPlayers: () -> Unit,
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
        title = { Text(title) },
        navigationIcon = {
          TextButton(onClick = onBack) { Text("戻る") }
        },
        actions = {
          if (onClickShare != null) {
            TextButton(onClick = onClickShare) { Text("共有") }
          }
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
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(horizontalScroll)
            .padding(8.dp)
        ) {
          Column(
            modifier = Modifier.width(920.dp)
          ) {
            HeaderRow()
            Column(
              modifier = Modifier.verticalScroll(verticalScroll)
            ) {
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
    }
  }

  if (uiState.isPlayerSelectVisible) {
    PlayerSelectSheet(
      allPlayers = allPlayers,
      pendingNewPlayerName = uiState.pendingNewPlayerName,
      pendingSelectedPlayerIds = uiState.pendingSelectedPlayerIds,
      onTogglePlayerSelection = onTogglePlayerSelection,
      onConfirmSelectedPlayers = onConfirmSelectedPlayers,
      onChangePendingName = onChangePendingNewPlayerName,
      onConfirmNewPlayer = onConfirmNewPlayer,
      onDismiss = onDismissPlayerSelect,
    )
  }

  if (uiState.isShareOptionsVisible) {
    AlertDialog(
      onDismissRequest = onDismissShareOptions,
      title = { Text("画像の扱い") },
      text = { Text("画像として保存するか、そのまま共有するか選んでください。") },
      confirmButton = {
        TextButton(onClick = onShareImage) { Text("共有") }
      },
      dismissButton = {
        TextButton(onClick = onSaveImage) { Text("画像保存") }
      },
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
