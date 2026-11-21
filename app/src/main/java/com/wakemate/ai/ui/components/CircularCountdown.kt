package com.wakemate.ai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakemate.ai.ui.theme.DarkCardBorder
import com.wakemate.ai.ui.theme.NeonCyan
import com.wakemate.ai.ui.theme.NeonPink

@Composable
fun CircularCountdown(
    totalSeconds: Int = 10,
    remainingSeconds: Int,
    size: Dp = 120.dp,
    strokeWidth: Dp = 8.dp,
    modifier: Modifier = Modifier
) {
    val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progressAnim"
    )

    val strokeColor by animateColorAsState(
        targetValue = if (remainingSeconds <= 3) NeonPink else NeonCyan,
        animationSpec = tween(durationMillis = 500),
        label = "colorAnim"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)

            // Background circle track
            drawCircle(
                color = DarkCardBorder.copy(alpha = 0.5f),
                style = stroke
            )

            // Foreground animated arc
            drawArc(
                color = strokeColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = stroke
            )
        }

        Text(
            text = "${remainingSeconds}s",
            color = strokeColor,
            fontSize = (size.value * 0.28).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
