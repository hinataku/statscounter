package com.hinataku.statscounter.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import com.hinataku.statscounter.data.Player
import com.hinataku.statscounter.ui.preview.PreviewPhone
import com.hinataku.statscounter.ui.preview.PreviewTablet
import com.hinataku.statscounter.ui.stats.blocks.HeaderRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerRow
import com.hinataku.statscounter.ui.stats.blocks.PlayerSelectSheet
import com.hinataku.statscounter.ui.stats.blocks.TotalRow

internal data class StatsTableWidths(
  val standard: Dp,
  val points: Dp,
) {
  val total: Dp = (standard * 8) + points
}

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
  snackbarHost: @Composable (() -> Unit) = {},
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
  val horizontalScroll = rememberScrollState()
  val verticalScroll = rememberScrollState()

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    snackbarHost = { snackbarHost() },
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
        modifier = Modifier.fillMaxWidth().weight(1f),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(8.dp)) {
          val minimumWidths = StatsTableWidths(
            standard = 92.dp,
            points = 64.dp,
          )
          val extraPerColumn = if (maxWidth > minimumWidths.total) {
            (maxWidth - minimumWidths.total) / 9
          } else {
            0.dp
          }
          val tableWidths = StatsTableWidths(
            standard = minimumWidths.standard + extraPerColumn,
            points = minimumWidths.points + extraPerColumn,
          )
          val needsScroll = maxWidth < minimumWidths.total
          Column(
            modifier = Modifier
              .fillMaxHeight()
              .then(
                if (needsScroll) {
                  Modifier.width(minimumWidths.total).horizontalScroll(horizontalScroll)
                } else {
                  Modifier.fillMaxWidth()
                }
              )
          ) {
            HeaderRow(tableWidths = tableWidths)
            Column(
              modifier = Modifier
                .weight(1f)
                .verticalScroll(verticalScroll)
            ) {
              uiState.players.forEach { player ->
                PlayerRow(
                  player = player,
                  tableWidths = tableWidths,
                  onPlus = { onPlus(player.id, it) },
                  onMinus = { onMinus(player.id, it) },
                  onLongPressName = { onLongPressPlayer(player.id) }
                )
              }
              TotalRow(
                players = uiState.players,
                tableWidths = tableWidths,
              )
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

@PreviewPhone
@PreviewTablet
@Composable
internal fun StatsTemplatePreviewContent() {
  StatsTemplate(
    title = "練習試合",
    uiState = StatsUiState(
      players = listOf(
        PlayerStats(1L, "田中", twoPointMade = 3, threePointMade = 1, assist = 2, rebound = 4, block = 1, steal = 1, turnover = 2),
        PlayerStats(2L, "鈴木", twoPointMade = 2, threePointMade = 2, assist = 5, rebound = 2, block = 0, steal = 3, turnover = 1),
        PlayerStats(3L, "佐藤", twoPointMade = 4, threePointMade = 0, assist = 1, rebound = 7, block = 2, steal = 0, turnover = 3),
      )
    ),
    allPlayers = emptyList(),
    onBack = {},
    onClickAddPlayer = {},
    onDismissShareOptions = {},
    onShareImage = {},
    onSaveImage = {},
    onDismissPlayerSelect = {},
    onDismissDeleteDialog = {},
    onTogglePlayerSelection = {},
    onConfirmSelectedPlayers = {},
    onChangePendingNewPlayerName = {},
    onConfirmNewPlayer = {},
    onLongPressPlayer = {},
    onConfirmDelete = {},
    onPlus = { _, _ -> },
    onMinus = { _, _ -> },
  )
}
