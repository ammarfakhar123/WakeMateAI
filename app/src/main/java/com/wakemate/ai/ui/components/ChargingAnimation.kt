package com.wakemate.ai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakemate.ai.ui.theme.NeonCyan
import com.wakemate.ai.ui.theme.NeonPink
import com.wakemate.ai.ui.theme.NeonPurple

@Composable
fun ChargingAnimation(
    modifier: Modifier = Modifier,
    isChargingDetected: Boolean = false,
    size: Dp = 160.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chargingPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val ringRadiusMultiplier by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val mainColor = if (isChargingDetected) NeonCyan else NeonPink

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Outer pulsing energy rings
        Canvas(modifier = Modifier.size(size)) {
            val centerRadius = (size.toPx() / 2f) * 0.7f

            drawCircle(
                color = mainColor.copy(alpha = ringAlpha * 0.3f),
                radius = centerRadius * ringRadiusMultiplier,
                style = Stroke(width = 3.dp.toPx())
            )

            drawCircle(
                color = NeonPurple.copy(alpha = ringAlpha * 0.2f),
                radius = centerRadius * 0.9f * ringRadiusMultiplier,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Center glowing icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.scale(if (isChargingDetected) 1f else pulseScale)
        ) {
            Icon(
                imageVector = if (isChargingDetected) Icons.Filled.Bolt else Icons.Filled.Power,
                contentDescription = "Charging Status",
                tint = mainColor,
                modifier = Modifier.size(size * 0.45f)
            )
        }
    }
}
