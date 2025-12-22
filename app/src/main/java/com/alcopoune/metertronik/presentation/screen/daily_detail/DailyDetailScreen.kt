package com.alcopoune.metertronik.presentation.screen.daily_detail

import DoubleBarChart
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alcopoune.metertronik.domain.model.DailyDetailsData
import com.alcopoune.metertronik.domain.model.HourlyData
import com.alcopoune.metertronik.presentation.components.card.MetricCard
import com.alcopoune.metertronik.presentation.components.card.PrimaryCard
import com.alcopoune.metertronik.presentation.screen.error.ErrorScreen
import com.alcopoune.metertronik.utils.DecimalFormater
import com.alcopoune.metertronik.utils.formatRupiah
import com.alcopoune.metertronik.utils.toHour

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    id: String,
    viewModel: DailyDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(
            deviceId = id,
            date = "2025-12-14"
        )
    }

    // Tampilkan ErrorScreen sebagai screen penuh jika terjadi error
    if (state is DailyDetailState.Error) {
        ErrorScreen(
            errorMessage = (state as DailyDetailState.Error).message,
            onRetry = {
                viewModel.load(
                    deviceId = id,
                    date = "2025-12-14"
                )
            },
            onBack = {
                navController.popBackStack()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Information $id",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.scrim
                )
            )
        }

    ) { innerPadding ->

        when (state) {
            DailyDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            is DailyDetailState.Error -> {
                // Error state sudah ditangani di atas dengan early return
            }

            is DailyDetailState.Success -> {
                val data = (state as DailyDetailState.Success).data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DailySummary(cost = data.daily.totalCost, energy = data.daily.energy)
                    AvgCard(data)

                    PrimaryCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "Hourly Value",
                                color = MaterialTheme.colorScheme.scrim
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LegendDot(color = MaterialTheme.colorScheme.error)
                                Text(
                                    text = "Max Value",
                                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                                    style = MaterialTheme.typography.bodySmall,
                                )

                                Spacer(Modifier.width(8.dp))

                                LegendDot(color = MaterialTheme.colorScheme.onSecondary)
                                Text(
                                    text = "Min Value",
                                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }

                        DoubleBarChart(hourly = data.hourly)
                    }

                    HourlySectionScroll(
                        hourlyData = data.hourly
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
    Spacer(Modifier.width(4.dp))
}


@Composable
fun DailySummary(
    cost : Double,
    energy : Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SummaryCard(
            icon = Icons.Default.Money,
            value = formatRupiah(cost),
            label = "Total Cost Energy",
            colorIcon = MaterialTheme.colorScheme.surface
        )

        SummaryCard(
            icon =Icons.Default.ElectricBolt,
            value = "${DecimalFormater(energy)} kWh",
            label = "Total Energy Used",
            colorIcon =MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Composable
private fun RowScope.SummaryCard(
    icon: ImageVector,
    value: String,
    label: String,
    colorIcon : Color,
) {
    PrimaryCard(
        modifier = Modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorIcon,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.scrim,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun AvgCard(data: DailyDetailsData) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricCard(
            title = "Avg Current",
            value = "${DecimalFormater(data.daily.avgCurrent)} A",
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.weight(1f)
        )

        MetricCard(
            title = "Avg Voltage",
            value = "${DecimalFormater(data.daily.avgVoltage)} V",
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )

        MetricCard(
            title = "Avg Power",
            value = "${DecimalFormater(data.daily.avgPower)} kW",
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlySectionScroll(
    hourlyData: List<HourlyData>
) {
    val defaultData = remember(hourlyData) {
        hourlyData.minByOrNull { it.ts }
    }

    var selectedData by remember(hourlyData) {
        mutableStateOf(defaultData)
    }

    Column {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(hourlyData) { item ->
                val hour = item.ts.toHour()

                HourCard(
                    hour = hour,
                    selectedHour = selectedData?.ts?.toHour(),
                    onClick = { selectedData = item }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedData?.let {
            Spacer(modifier = Modifier.height(16.dp))
            HourlyAverageCard(data = it)
            Spacer(modifier = Modifier.height(16.dp))
            HourlySummary(data = it)
        }
    }
}


@Composable
fun HourlySummary(
    data : HourlyData
) {
    SummaryCard(
        title = "Total Cost",
        value = formatRupiah(data.totalCost),
        icon = Icons.Default.Money,
        iconBgColor = MaterialTheme.colorScheme.surface.copy(0.7f),
    )
    Spacer(modifier = Modifier.height(16.dp))
    SummaryCard(
        title = "Total Energy",
        value = "${DecimalFormater(data.energy)} kWh",
        icon = Icons.Default.ElectricBolt,
        iconBgColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
    )
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
) {
    PrimaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column() {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.scrim
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.scrim,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HourlyAverageCard(
    data : HourlyData
) {
    PrimaryCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricItem("Avg Current", "${DecimalFormater(data.avgCurrent)} A", icon = Icons.Default.GraphicEq, color = MaterialTheme.colorScheme.tertiary)
                MetricItem("Avg Power", "${DecimalFormater(data.avgPower)} W", icon = Icons.Default.Lightbulb, color = MaterialTheme.colorScheme.secondary)
                MetricItem("Avg Voltage", "${DecimalFormater(data.avgVoltage)} V", icon = Icons.Default.Memory, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun HourCard(
    hour: Int,
    selectedHour: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedHour == hour

    Card(
        modifier = modifier
            .width(72.dp)
            .height(90.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.secondary
            else
                MaterialTheme.colorScheme.secondary.copy(0.7f)
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.background
                else
                    MaterialTheme.colorScheme.background,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format("%02d:00", hour),
                fontWeight = FontWeight.Medium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.background
                else
                    MaterialTheme.colorScheme.background
            )
        }
    }
}
