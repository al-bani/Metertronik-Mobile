package com.alcopoune.metertronik.presentation.screen.dashboard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.alcopoune.metertronik.domain.model.DashboardSummaryData
import com.alcopoune.metertronik.domain.model.ElectricityRealtime
import com.alcopoune.metertronik.presentation.components.LoadingDots
import com.alcopoune.metertronik.presentation.components.card.MetricCard
import com.alcopoune.metertronik.presentation.components.card.PrimaryCard
import com.alcopoune.metertronik.presentation.components.chart.LineChartCustom
import com.alcopoune.metertronik.presentation.components.chart.LinearProgressBar
import com.alcopoune.metertronik.presentation.components.chart.PowerGaugeChart
import com.alcopoune.metertronik.presentation.navigation.MainBottomBar
import com.alcopoune.metertronik.presentation.screen.error.ErrorScreen
import com.alcopoune.metertronik.utils.RealtimePulse
import com.alcopoune.metertronik.utils.daysBeforeEndOfMonth
import com.alcopoune.metertronik.utils.formatDailyLabel
import com.alcopoune.metertronik.utils.formatMonthlyLabel
import com.alcopoune.metertronik.utils.formatRupiah
import com.alcopoune.metertronik.utils.progressToEndOfMonth
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val dashboardState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.connectWebSocket("device-001")
        viewModel.loadDashboardData(
            deviceId = "device-001",
            date = "2025-12-11"
        )
    }

    if (dashboardState is DashboardState.Error) {
        ErrorScreen(
            errorMessage = (dashboardState as DashboardState.Error).message,
            onRetry = {
                viewModel.loadDashboardData(
                    deviceId = "device-001",
                    date = "2025-12-11"
                )
            },
            onBack = {
                navController.popBackStack()
            }
        )
        return
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

                is DashboardState.Success -> {
                    val data = (dashboardState as DashboardState.Success).data

                    CostSummaryCard(
                        monthlyCost = data.monthly.totalCost
                    )

                    LineChart(data)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Real Time Data",
                            style = MaterialTheme.typography.titleLarge
                        )

                        if (dashboardState.isRealtimeConnected) {
                            LoadingDots()
                        }
                    }

                    RealtimeCard(realtimeData = dashboardState.realtimeData)
                    PowerGaugeCard(realtimeData = dashboardState.realtimeData)

                    EfficiencyElectric(dashboardState.realtimeData?.powerFactor?.toFloat() ?: 0f)

                    Spacer(modifier = Modifier.height(16.dp))
                }

                else -> {}
            }

        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun LineChart(
    data: DashboardSummaryData
) {
    val dailyDataPoints = remember(data) {
        data.daily.map { it.totalCost.toFloat() }
    }

    val dailyLabels = remember(data) {
        data.daily.map { formatDailyLabel(it.day) }
    }

    val monthlyDataPoints = remember(data) {
        data.monthlyList.map { it.totalCost.toFloat() }
    }

    val monthlyLabels = remember(data) {
        data.monthlyList.map { formatMonthlyLabel(it.month) }
    }

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
                data = if (isDaily) dailyDataPoints else monthlyDataPoints,
                labels = if (isDaily) dailyLabels else monthlyLabels
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
        modifier = modifier
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CostSummaryCard(
    monthlyCost: Double
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
                text = formatRupiah(monthlyCost),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ElectricBolt,
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
                text = daysBeforeEndOfMonth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f)
            )
        }

        LinearProgressBar(progressToEndOfMonth())

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
                    containerColor = MaterialTheme.colorScheme.secondary,
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

@SuppressLint("DefaultLocale")
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
        RealtimePulse(
            modifier = Modifier.weight(1f)
        ) {
            MetricCard(
                title = "Current",
                value = "$current A",
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        RealtimePulse(
            modifier = Modifier.weight(1f)
        ) {
            MetricCard(
                title = "Voltage",
                value = "$voltage V",
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            )
        }

        RealtimePulse(
            modifier = Modifier.weight(1f)
        ) {
            MetricCard(
                title = "Power",
                value = power,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

@Composable
fun PowerGaugeCard(
    realtimeData: ElectricityRealtime?
) {
    val surgeWatt: Double = realtimeData?.powerSurge ?: 0.0
    val surgePercent: Double = realtimeData?.powerSurgePercentage ?: 0.0

    val indicator: Float = when {
        surgeWatt > 500.0 || surgePercent > 30.0 -> 875f
        surgeWatt > 200.0 || surgePercent > 15.0 -> 625f
        surgeWatt > 50.0 || surgePercent > 5.0 -> 375f
        surgeWatt == 0.0 || surgePercent == 0.0 -> 0f
        else -> 125f
    }

    PrimaryCard {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Power Surge Indicator",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                )

                RealtimePulse {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", surgePercent)}%+",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PowerGaugeChart(power = indicator)

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

                    RealtimePulse {
                        Text(
                            text = "${surgeWatt.toInt()} W",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.scrim.copy(0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }

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

@Composable
fun EfficiencyElectric(
    progress: Float
) {
    val barColors = when {
        progress < 0.5f -> {
            listOf(
                MaterialTheme.colorScheme.error
            )
        }

        progress < 0.7f -> {
            listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.tertiary
            )
        }

        progress < 0.85f -> {
            listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.onTertiary
            )
        }

        else -> {
            listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.onTertiary,
                MaterialTheme.colorScheme.surface
            )
        }
    }

    PrimaryCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Efficiency Electricity Rate",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.scrim.copy(0.8f)
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

        RealtimePulse {
            LinearProgressBar(
                progress = progress,
                barColors = barColors
            )
        }



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