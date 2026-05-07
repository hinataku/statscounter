package com.hinataku.statscounter.ui.stats.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.ui.stats.PlayerStats
import com.hinataku.statscounter.ui.stats.StatsUiState

@Composable
fun StatsShareCard(
  gameName: String,
  uiState: StatsUiState,
  modifier: Modifier = Modifier,
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Column(
      modifier = Modifier
        .background(Color(0xFFF8FAFC))
        .padding(20.dp)
    ) {
      Text(
        text = gameName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF111827),
      )
      Box(modifier = Modifier.height(16.dp))
      ShareHeaderRow()
      uiState.players.forEach { player ->
        SharePlayerRow(player)
      }
      ShareTotalRow(uiState.players)
    }
  }
}

@Composable
private fun ShareHeaderRow() {
  Row(verticalAlignment = Alignment.CenterVertically) {
    ShareHeaderCell("名前", 120.dp)
    ShareHeaderCell("2P", 80.dp)
    ShareHeaderCell("3P", 80.dp)
    ShareHeaderCell("PTS", 80.dp)
    ShareHeaderCell("AST", 80.dp)
    ShareHeaderCell("REB", 80.dp)
    ShareHeaderCell("BLK", 80.dp)
    ShareHeaderCell("STL", 80.dp)
    ShareHeaderCell("TO", 80.dp)
  }
}

@Composable
private fun ShareHeaderCell(text: String, width: Dp) {
  Box(
    modifier = Modifier
      .width(width)
      .height(44.dp)
      .background(Color(0xFFE5E7EB))
      .border(BorderStroke(0.5.dp, Color(0xFF9CA3AF))),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = text, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
  }
}

@Composable
private fun SharePlayerRow(player: PlayerStats) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    ShareValueCell(player.name, 120.dp, height = 60.dp, isName = true)
    ShareValueCell(player.twoPointMade.toString(), 80.dp)
    ShareValueCell(player.threePointMade.toString(), 80.dp)
    ShareValueCell(
      text = player.points.toString(),
      width = 80.dp,
      background = Color(0xFFFFF7ED),
      textColor = Color(0xFFEA580C),
      fontSize = 16.sp,
    )
    ShareValueCell(player.assist.toString(), 80.dp)
    ShareValueCell(player.rebound.toString(), 80.dp)
    ShareValueCell(player.block.toString(), 80.dp)
    ShareValueCell(player.steal.toString(), 80.dp)
    ShareValueCell(player.turnover.toString(), 80.dp)
  }
}

@Composable
private fun ShareTotalRow(players: List<PlayerStats>) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    ShareTotalCell("合計", 120.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.twoPointMade }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.threePointMade }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.points }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.assist }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.rebound }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.block }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.steal }.toString(), 80.dp, fontSize = 16.sp)
    ShareTotalCell(players.sumOf { it.turnover }.toString(), 80.dp, fontSize = 16.sp)
  }
}

@Composable
private fun ShareValueCell(
  text: String,
  width: Dp,
  height: Dp = 60.dp,
  isName: Boolean = false,
  background: Color = Color.White,
  textColor: Color = Color(0xFF111827),
  fontSize: TextUnit = if (isName) 16.sp else 28.sp,
) {
  Box(
    modifier = Modifier
      .width(width)
      .height(height)
      .background(background)
      .border(BorderStroke(0.5.dp, Color(0xFFD1D5DB)))
      .padding(horizontal = 6.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = textColor,
      fontWeight = if (isName) FontWeight.Medium else FontWeight.Bold,
      textAlign = TextAlign.Center,
      fontSize = fontSize,
    )
  }
}

@Composable
private fun ShareTotalCell(
  text: String,
  width: Dp,
  fontSize: TextUnit = 28.sp,
) {
  Box(
    modifier = Modifier
      .width(width)
      .height(52.dp)
      .background(Color(0xFF111827))
      .border(BorderStroke(0.5.dp, Color.White)),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = fontSize,
    )
  }
}
