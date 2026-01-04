package com.alcopoune.metertronik.presentation.components.loading

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientSearchLoader(
    loaderSize: Dp = 220.dp,
    strokeWidth: Dp = 6.dp,
    isScanning: Boolean = true,
    onScanNowClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconRotation"
    )

    Box(
        modifier = Modifier.size(loaderSize),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            val gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF2196F3),
                    Color(0xFF7B1FA2)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height) // ✅ FIX
            )

            drawCircle(
                brush = gradient,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        if (isScanning){
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Searching",
                tint = Color(0xFF5E35B1),
                modifier = Modifier
                    .size(36.dp)
                    .graphicsLayer(
                        scaleX = iconScale,
                        scaleY = iconScale,
                        rotationZ = iconRotation
                    )
            )
        } else {
            Text(
                text = "Scan Now",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.clickable(
                    onClick = {
                        onScanNowClick()
                    }
                )
            )
        }


    }
}
