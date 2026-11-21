package com.wakemate.ai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakemate.ai.ui.theme.NeonAmber
import com.wakemate.ai.ui.theme.NeonCyan
import com.wakemate.ai.ui.theme.NeonPink

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (color, label) = when (priority.uppercase()) {
        "HIGH" -> Pair(NeonPink, "HIGH")
        "MEDIUM" -> Pair(NeonAmber, "MED")
        else -> Pair(NeonCyan, "LOW")
    }

    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
