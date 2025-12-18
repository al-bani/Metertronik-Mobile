package com.alcopoune.metertronik.presentation.screen.daily_details

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alcopoune.metertronik.presentation.components.MetricCard

@Composable
fun DetailsHistoryDayScreen(
    id: String,
    modifier: Modifier = Modifier
) {
    DetailsCommonContent(
        title = "History Hari Ini (ID: $id)",
        modifier = modifier
    )
}

@Composable
private fun DetailsCommonContent(
    title: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        CostAndHourlyHistoryCard()

        ScrollableChartsSection()

        DailyAverageCards()

        EfficiencyProgressSection()

        HourlyHistoryList()
    }
}

@Composable
private fun CostAndHourlyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Biaya Hari Ini",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Rp 35.000",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "History per jam",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScrollableChartsSection() {
    val currentSeries = remember {
        listOf(0.3f, 0.5f, 0.4f, 0.8f, 0.6f, 0.7f, 0.9f, 0.5f)
    }
    val voltageSeries = remember {
        listOf(0.6f, 0.7f, 0.65f, 0.8f, 0.75f, 0.7f, 0.85f, 0.8f)
    }
    val powerSeries = remember {
        listOf(0.2f, 0.4f, 0.35f, 0.6f, 0.55f, 0.7f, 0.65f, 0.5f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Grafik Current, Voltage, Power",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ChartLine(
                    title = "Current (A)",
                    color = MaterialTheme.colorScheme.primary,
                    points = currentSeries
                )
                ChartLine(
                    title = "Voltage (V)",
                    color = MaterialTheme.colorScheme.tertiary,
                    points = voltageSeries
                )
                ChartLine(
                    title = "Power (kW)",
                    color = MaterialTheme.colorScheme.secondary,
                    points = powerSeries
                )
            }
        }
    }
}

@Composable
private fun ChartLine(
    title: String,
    color: Color,
    points: List<Float>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            if (points.size < 2) return@Canvas

            val padding = 24f
            val width = size.width - padding * 2
            val height = size.height - padding * 2
            val maxValue = (points.maxOrNull() ?: 1f).coerceAtLeast(0.01f)
            val stepX = width / (points.size - 1)

            val path = Path()
            points.forEachIndexed { index, value ->
                val x = padding + stepX * index
                val normalizedY = (value / maxValue).coerceIn(0f, 1f)
                val y = padding + height * (1f - normalizedY)

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            points.forEachIndexed { index, value ->
                val x = padding + stepX * index
                val normalizedY = (value / maxValue).coerceIn(0f, 1f)
                val y = padding + height * (1f - normalizedY)

                drawCircle(
                    color = color,
                    radius = 8f,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun DailyAverageCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

    }
}


@Composable
private fun EfficiencyProgressSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Efisiensi Listrik",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "75%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }
}

@Composable
private fun HourlyHistoryList() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "History Per Jam",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            repeat(6) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${index * 4}:00 - ${(index + 1) * 4}:00",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Rata2: 7.2 A / 220 V / 1.5 kW",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Biaya: Rp 6.000",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Total: 0.8 kWh",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (index != 5) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


