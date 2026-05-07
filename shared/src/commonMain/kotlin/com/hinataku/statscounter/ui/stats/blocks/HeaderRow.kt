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
import com.hinataku.statscounter.ui.preview.PreviewPhone
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@PreviewPhone
@Composable
internal fun HeaderRow() {
  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    HeaderCell("名前", 92.dp)
    HeaderCell("2P", 92.dp)
    HeaderCell("3P", 92.dp)
    HeaderCell("PTS", 64.dp)
    HeaderCell("AST", 92.dp)
    HeaderCell("REB", 92.dp)
    HeaderCell("BLK", 92.dp)
    HeaderCell("STL", 92.dp)
    HeaderCell("TO", 92.dp)
    Spacer(modifier = Modifier.weight(1f).height(44.dp))
  }
}

@Composable
private fun HeaderCell(text: String, width: Dp) {
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
