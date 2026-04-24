package com.hinataku.statscounter.ui.home.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.hinataku.statscounter.ui.home.PlayerSummary
import kotlin.math.roundToInt

@Composable
internal fun PlayerSummaryCard(summary: PlayerSummary) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = summary.player.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f),
        )
        Text(
          text = "${summary.gamesPlayed}試合",
          style = MaterialTheme.typography.bodySmall,
          color = Color.Gray,
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
          .horizontalScroll(rememberScrollState()),
      ) {
        StatCell("PTS", summary.totalPoints, summary.avgPoints)
        StatCell("2P", summary.total2P, summary.total2P.toDouble() / summary.gamesPlayed)
        StatCell("3P", summary.total3P, summary.total3P.toDouble() / summary.gamesPlayed)
        StatCell("AST", summary.totalAssists, summary.avgAssists)
        StatCell("REB", summary.totalRebounds, summary.avgRebounds)
        StatCell("BLK", summary.totalBlocks, summary.totalBlocks.toDouble() / summary.gamesPlayed)
        StatCell("STL", summary.totalSteals, summary.totalSteals.toDouble() / summary.gamesPlayed)
        StatCell("TO", summary.totalTurnovers, summary.totalTurnovers.toDouble() / summary.gamesPlayed)
      }
    }
  }
}

@Composable
private fun StatCell(label: String, total: Int, avg: Double) {
  Column(
    modifier = Modifier
      .width(56.dp)
      .border(BorderStroke(0.5.dp, Color(0xFFE5E7EB))),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(24.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(28.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = total.toString(),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
      )
    }
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(22.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = "avg ${(avg * 10).roundToInt() / 10.0}",
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        textAlign = TextAlign.Center,
      )
    }
  }
}
