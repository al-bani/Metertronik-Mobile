package com.alcopoune.metertronik.presentation.components.chart

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun PowerGaugeChart(
    power: Float,
    maxPower: Float = 1000f,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
) {
    Canvas(
        modifier = modifier
            .aspectRatio(2f) // width : height = 2 : 1 (pas setengah lingkaran)
    ) {

        val strokeWidth = size.width * 0.07f
        val arcRadius = size.width / 2
        val center = Offset(size.width / 2, size.height)

        fun valueToAngle(value: Float): Float {
            return 180f + (value / maxPower).coerceIn(0f, 1f) * 180f
        }

        fun drawZone(
            startValue: Float,
            endValue: Float,
            color: Color
        ) {
            drawArc(
                color = color,
                startAngle = valueToAngle(startValue),
                sweepAngle = valueToAngle(endValue) - valueToAngle(startValue),
                useCenter = false,
                style = Stroke(strokeWidth),
                topLeft = Offset(
                    center.x - arcRadius,
                    center.y - arcRadius
                ),
                size = Size(arcRadius * 2, arcRadius * 2)
            )
        }

        // =====================
        // ZONA (250 per zona)
        // =====================
        drawZone(0f, 250f, Color(0xFF4CAF50))   // Hijau
        drawZone(250f, 500f, Color(0xFFFFEB3B)) // Kuning
        drawZone(500f, 750f, Color(0xFFFF9800)) // Orange
        drawZone(750f, 1000f, Color(0xFFF44336)) // Merah

        // =====================
        // NEEDLE
        // =====================
        val needleAngle =
            Math.toRadians(valueToAngle(power).toDouble())

        val needleLength = arcRadius * 0.7f

        val needleEnd = Offset(
            x = center.x + needleLength * cos(needleAngle).toFloat(),
            y = center.y + needleLength * sin(needleAngle).toFloat()
        )

        drawLine(
            color = Color.Gray,
            start = center,
            end = needleEnd,
            strokeWidth = strokeWidth * 0.35f,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color.Gray,
            radius = strokeWidth * 0.45f,
            center = center
        )
    }
}

