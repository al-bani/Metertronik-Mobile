package com.alcopoune.metertronik.presentation.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun LineChartCustom(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF6200EE),
    pointColor: Color = Color.White
) {
    if (data.isEmpty() || data.size != labels.size) return

    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(fontSize = 12.sp, color = Color.Gray)

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // === FIXED CONSTANTS (SATU SUMBER KEBENARAN) ===
    val sidePadding = with(density) { 16.dp.toPx() }
    val labelPadding = with(density) { 26.dp.toPx() }

    // hitung tinggi label REAL
    val labelHeight = textMeasurer
        .measure(labels.first(), textStyle)
        .size.height
        .toFloat()

    val labelSpace = labelHeight + labelPadding

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .pointerInput(Unit) {
                    detectTapGestures { tap ->
                        val chartHeight = size.height - labelSpace
                        val max = data.maxOrNull() ?: 1f
                        val min = data.minOrNull() ?: 0f
                        val range = (max - min).coerceAtLeast(1f)
                        val spacing = (size.width - sidePadding * 2) / (data.size - 1)

                        data.indices.forEach { i ->
                            val x = sidePadding + spacing * i
                            val y = chartHeight - ((data[i] - min) / range) * chartHeight

                            if (abs(tap.x - x) < 40f && abs(tap.y - y) < 40f) {
                                selectedIndex = if (selectedIndex == i) null else i
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            val chartHeight = height - labelSpace
            val spacing = (width - sidePadding * 2) / (data.size - 1)
            val max = data.maxOrNull() ?: 1f
            val min = data.minOrNull() ?: 0f
            val range = (max - min).coerceAtLeast(1f)

            fun point(i: Int, v: Float): Offset {
                val x = sidePadding + spacing * i
                val y = chartHeight - ((v - min) / range) * chartHeight
                return Offset(x, y)
            }

            // === LINE ===
            val path = Path()
            val first = point(0, data[0])
            path.moveTo(first.x, first.y)

            for (i in 0 until data.lastIndex) {
                val p1 = point(i, data[i])
                val p2 = point(i + 1, data[i + 1])

                val controlX = (p1.x + p2.x) / 2f

                path.cubicTo(
                    controlX, p1.y,   // control point 1
                    controlX, p2.y,   // control point 2
                    p2.x, p2.y        // end point
                )
            }


            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // === POINTS + LABELS ===
            data.forEachIndexed { i, v ->
                val c = point(i, v)
                val selected = i == selectedIndex

                drawCircle(
                    color = lineColor,
                    center = c,
                    radius = if (selected) 18f else 14f
                )
                drawCircle(
                    color = pointColor,
                    center = c,
                    radius = if (selected) 12f else 10f
                )

                val label = labels[i]
                val layout = textMeasurer.measure(label, textStyle)

                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    style = textStyle,
                    topLeft = Offset(
                        x = c.x - layout.size.width / 2f,
                        y = chartHeight + labelPadding
                    )
                )

                // === TOOLTIP ===
                if (selected) {
                    val valueText = v.toInt().toString()
                    val tooltipStyle = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val tooltip = textMeasurer.measure(valueText, tooltipStyle)
                    val w = tooltip.size.width + 30f
                    val h = tooltip.size.height + 20f

                    val topLeft = Offset(c.x - w / 2f, c.y - h - 16f)

                    drawRoundRect(
                        color = Color(0xFF333333),
                        topLeft = topLeft,
                        size = Size(w, h),
                        cornerRadius = CornerRadius(8f)
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = valueText,
                        style = tooltipStyle,
                        topLeft = Offset(topLeft.x + 15f, topLeft.y + 10f)
                    )
                }
            }
        }
    }
}
