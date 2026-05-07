package com.hinataku.statscounter.ui.stats.blocks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hinataku.statscounter.ui.stats.PlayerStats
import com.hinataku.statscounter.ui.stats.StatType

@Composable
internal fun PlayerRow(
    player: PlayerStats,
    onPlus: (StatType) -> Unit,
    onMinus: (StatType) -> Unit,
    onLongPressName: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NameCell(name = player.name, onLongPress = onLongPressName)
        NumberCell(player.twoPointMade, StatType.TwoPoint, onPlus, onMinus)
        NumberCell(player.threePointMade, StatType.ThreePoint, onPlus, onMinus)
        ReadOnlyCell(text = player.points.toString(), fontSize = 16.sp)
        NumberCell(player.assist, StatType.Assist, onPlus, onMinus)
        NumberCell(player.rebound, StatType.Rebound, onPlus, onMinus)
        NumberCell(player.block, StatType.Block, onPlus, onMinus)
        NumberCell(player.steal, StatType.Steal, onPlus, onMinus)
        NumberCell(player.turnover, StatType.Turnover, onPlus, onMinus)
        Spacer(modifier = Modifier.weight(1f).height(72.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NameCell(name: String, onLongPress: () -> Unit) {
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(72.dp)
            .border(BorderStroke(0.5.dp, Color(0xFFD1D5DB)))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
                onLongClick = onLongPress,
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun NumberCell(
    value: Int,
    type: StatType,
    onPlus: (StatType) -> Unit,
    onMinus: (StatType) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .width(92.dp)
            .height(72.dp)
            .border(BorderStroke(0.5.dp, Color(0xFFD1D5DB))),
    ) {
        Column(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPlus(type)
                    },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onMinus(type)
                    },
            )
        }
        Text(
            text = value.toString(),
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )
    }
}

@Composable
private fun ReadOnlyCell(text: String, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .height(72.dp)
            .background(Color(0xFFFFF7ED))
            .border(BorderStroke(0.5.dp, Color(0xFFD1D5DB))),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEA580C),
            fontSize = fontSize,
        )
    }
}
