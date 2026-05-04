package com.hinataku.statscounter.ui.stats.blocks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.data.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayerSelectSheet(
  allPlayers: List<Player>,
  pendingNewPlayerName: String,
  pendingSelectedPlayerIds: Set<Long>,
  onTogglePlayerSelection: (Player) -> Unit,
  onConfirmSelectedPlayers: () -> Unit,
  onChangePendingName: (String) -> Unit,
  onConfirmNewPlayer: () -> Unit,
  onDismiss: () -> Unit,
) {
  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
  ) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text("選手を選択", style = MaterialTheme.typography.titleMedium)

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
      ) {
        OutlinedTextField(
          value = pendingNewPlayerName,
          onValueChange = onChangePendingName,
          singleLine = true,
          placeholder = { Text("新規選手名") },
          modifier = Modifier.weight(1f),
          keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
          keyboardActions = KeyboardActions(onDone = { onConfirmNewPlayer() }),
        )
        TextButton(onClick = onConfirmNewPlayer) { Text("追加") }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider()
      Spacer(modifier = Modifier.height(4.dp))

      LazyColumn {
        items(allPlayers, key = { it.id }) { player ->
          val selected = player.id in pendingSelectedPlayerIds
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable { onTogglePlayerSelection(player) }
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Checkbox(
              checked = selected,
              onCheckedChange = { onTogglePlayerSelection(player) },
            )
            Text(
              text = player.name,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.weight(1f),
            )
          }
          HorizontalDivider()
        }
      }

      Spacer(modifier = Modifier.height(12.dp))
      TextButton(
        onClick = onConfirmSelectedPlayers,
        modifier = Modifier.align(Alignment.End),
      ) {
        Text("完了")
      }
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
