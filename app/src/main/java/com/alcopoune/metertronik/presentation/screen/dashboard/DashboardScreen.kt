package com.alcopoune.metertronik.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import com.alcopoune.metertronik.presentation.components.LoadingDots
import com.alcopoune.metertronik.presentation.components.MetricCard
import com.alcopoune.metertronik.presentation.components.PrimaryCard
import com.alcopoune.metertronik.presentation.components.chart.LineChartCustom
import com.alcopoune.metertronik.presentation.components.chart.LinearProgressBar
import com.alcopoune.metertronik.presentation.components.chart.PowerGaugeChart
import com.alcopoune.metertronik.presentation.navigation.MainBottomBar
import com.alcopoune.metertronik.presentation.theme.Orange

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.uiState.collectAsState()
    val realtimeData by viewModel.realtimeData.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.connectWebSocket("device-001")
        viewModel.loadDashboardData(
            deviceId = "device-001",
            date = "2025-12-11"
        )
    }

    Scaffold(
        bottomBar = {
            MainBottomBar(navController = navController)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            when (dashboardState) {
                is DashboardState.Loading -> {
                    LoadingDots()
                }

                is DashboardState.Error -> {
                    Text(
                        text = (dashboardState as DashboardState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is DashboardState.Success -> {
                    val data =
                        (dashboardState as DashboardState.Success).data

                    CostSummaryCard(
                        monthlyCost = data.monthly.totalCost.toString(),
                        progress = 1000f
                    )

                    LineChart()
                }
            }

            // ========================
            // Realtime Section
            // ========================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Real Time Data",
                    style = MaterialTheme.typography.titleLarge
                )

                if (isConnected) {
                    LoadingDots()
                }
            }

            RealtimeCard(realtimeData = realtimeData)
            PowerGaugeCard(realtimeData = realtimeData)

            EfficiencyElectric()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun EfficiencyElectric() {
    PrimaryCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Efficiency Electricity Rate",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.scrim
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0.0%",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = "100.0%",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.surface
            )
        }
        LinearProgressBar(
            progress = 0.7f,
            barColors = listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.onTertiary,
                MaterialTheme.colorScheme.surface,
                )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Higher Value is better",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.scrim
            )
        }
    }
}

@Composable
private fun LineChart() {

    val dailyDataPoints = listOf(10f, 50f, 20f, 80f, 40f, 90f, 30f)
    val dailyLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val monthlyDataPoints = listOf(120f, 150f, 180f, 200f, 160f, 190f, 170f, 210f, 230f, 200f, 180f, 220f)
    val monthlyLabels = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

    var isDaily by remember { mutableStateOf(true) }

    PrimaryCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DailyMonthlyTextSwitch(
                    isDailySelected = isDaily,
                    onChange = { isDaily = it }
                )

                Icon(
                    imageVector = Icons.Filled.MoreHoriz,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LineChartCustom(
                data = dailyDataPoints,
                labels = dailyLabels
            )
        }
    }
}

@Composable
fun DailyMonthlyTextSwitch(
    isDailySelected: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = MaterialTheme.colorScheme.onSecondary
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Row(
        modifier = modifier,
    ) {
        Text(
            text = "Daily",
            color = if (isDailySelected) selectedColor else unselectedColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isDailySelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier
                .clickable { onChange(true) }
                .padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Monthly",
            color = if (!isDailySelected) selectedColor else unselectedColor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (!isDailySelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier
                .clickable { onChange(false) }
                .padding(vertical = 8.dp)
        )
    }
}


@Composable
fun CostSummaryCard(
    monthlyCost: String,
    progress: Float
) {
    PrimaryCard {
        Text(
            text = "Total Cost this Month",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.scrim.copy(0.7f)
        )
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = monthlyCost,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Money,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timelapse,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "27 days before 30 days",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f)
            )
        }

        LinearProgressBar(progress)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Download,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Export Data",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "Today Data",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun RealtimeCard(
    realtimeData: ElectricityRealtime?
) {
    val current = realtimeData?.current
        ?.let { String.format("%.2f", it) }
        ?: "0.00"

    val voltage = realtimeData?.voltage
        ?.let { String.format("%.1f", it) }
        ?: "0.0"

    val power = realtimeData?.power?.let {
        if (it >= 1000) {
            "${String.format("%.2f", it / 1000)} kW"
        } else {
            "${String.format("%.0f", it)} W"
        }
    } ?: "0 W"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            title = "Current",
            value = "$current A",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.tertiary
        )

        MetricCard(
            title = "Voltage",
            value = "$voltage V",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondary
        )

        MetricCard(
            title = "Power",
            value = power,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun PowerGaugeCard(
    realtimeData: ElectricityRealtime?
) {
    val power = realtimeData?.power?.toFloat() ?: 0f

    PrimaryCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Power Surge Indicator",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim.copy(0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PowerGaugeChart(power = power)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Safe",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                    Text(
                        text = "${power.toInt()} W",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.scrim
                    )
                    Text(
                        text = "Danger",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
