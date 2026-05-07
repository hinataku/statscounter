package com.hinataku.statscounter.ui.stats.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hinataku.statscounter.ui.stats.PlayerStats

@Composable
internal fun TotalRow(players: List<PlayerStats>) {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    TotalCell("合計", 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.twoPointMade }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.threePointMade }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.points }.toString(), 64.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.assist }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.rebound }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.block }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.steal }.toString(), 92.dp, fontSize = 16.sp)
    TotalCell(players.sumOf { it.turnover }.toString(), 92.dp, fontSize = 16.sp)
    Spacer(modifier = Modifier.weight(1f).height(52.dp))
  }
}

@Composable
private fun TotalCell(
  text: String,
  width: Dp,
  fontSize: androidx.compose.ui.unit.TextUnit = 28.sp,
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
