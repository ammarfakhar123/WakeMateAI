package com.wakemate.ai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakemate.ai.ui.theme.DarkCardBg
import com.wakemate.ai.ui.theme.DarkCardBorder
import com.wakemate.ai.ui.theme.NeonCyan
import com.wakemate.ai.ui.theme.NeonPurple

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderColor: Color = DarkCardBorder,
    glowAccent: Color? = null,
    backgroundColor: Color = DarkCardBg,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val borderStroke = if (glowAccent != null) {
        BorderStroke(
            width = 1.2.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    glowAccent.copy(alpha = 0.8f),
                    glowAccent.copy(alpha = 0.2f)
                )
            )
        )
    } else {
        BorderStroke(1.dp, borderColor)
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .border(borderStroke, shape),
        shape = shape,
        color = backgroundColor,
        shadowElevation = 8.dp
    ) {
        content()
    }
}
