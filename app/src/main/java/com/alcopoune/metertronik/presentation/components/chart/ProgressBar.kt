package com.alcopoune.metertronik.presentation.components.chart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LinearProgressBar(
    progress : Float,
    modifier: Modifier = Modifier,
    trackColor : Color = MaterialTheme.colorScheme.background,
    barColors: List<Color> = listOf(MaterialTheme.colorScheme.tertiary,MaterialTheme.colorScheme.error),
){
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress) // Isi lebar sesuai progress
                .fillMaxHeight()
                .background(
                    brush = Brush.horizontalGradient(barColors) // Pakai Gradasi
                )
        )
    }
}