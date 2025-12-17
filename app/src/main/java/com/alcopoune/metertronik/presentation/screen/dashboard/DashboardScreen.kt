package com.alcopoune.metertronik.presentation.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alcopoune.metertronik.presentation.components.MetricCard
import com.alcopoune.metertronik.presentation.components.PrimaryCard
import com.alcopoune.metertronik.presentation.components.chart.LineChartCustom
import com.alcopoune.metertronik.presentation.components.chart.LinearProgressBar
import com.alcopoune.metertronik.presentation.components.chart.PowerGaugeChart

@Composable
fun DashboardScreen(
    onNavigateToDetailsHistoryDay: () -> Unit,
    onNavigateToDetailsCurrentDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val monthlyCost = remember { "Rp 250.000" }
    val monthlyProgress = remember { 0.6f }

    MaterialTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CostSummaryCard(
                monthlyCost = monthlyCost,
                progress = monthlyProgress,
                onDetailsHistoryClick = onNavigateToDetailsHistoryDay,
                onDetailsCurrentClick = onNavigateToDetailsCurrentDay
            )
            LineChart()
            RealtimeCard()
            PowerGaugeCard()

        }
    }
}

@Composable
private fun LineChart() {
    val dataPoints = listOf(10f, 50f, 20f, 80f, 40f, 90f, 30f)
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    PrimaryCard {
        LineChartCustom(data = dataPoints, labels = labels)
    }

}
@Composable
fun CostSummaryCard(
    monthlyCost: String,
    progress: Float,
    onDetailsHistoryClick: () -> Unit,
    onDetailsCurrentClick: () -> Unit
) {
    PrimaryCard {
            Text(
                text = "Biaya Bulan Ini",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.scrim
            )
            Text(
                text = monthlyCost,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Progress Tagihan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f)
            )

            LinearProgressBar(progress)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDetailsHistoryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor =  MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("History Hari Ini")
                }
                Button(
                    onClick = onDetailsCurrentClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Status Saat Ini")
                }
            }
        }
}


@Composable
fun RealtimeCard() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            title = "Current",
            value = "7.5 A",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.tertiary
        )
        MetricCard(
            title = "Voltage",
            value = "221 V",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondary
        )
        MetricCard(
            title = "Power",
            value = "1.4 kW",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PowerGaugeCard() {
    var power by remember { mutableStateOf(720f) }
    PrimaryCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Engine Power",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                PowerGaugeChart(power = power)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${power.toInt()} W",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.scrim
                )
            }
        }
    }
}
