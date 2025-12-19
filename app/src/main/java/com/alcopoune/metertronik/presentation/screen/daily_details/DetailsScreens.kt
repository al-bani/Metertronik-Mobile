package com.alcopoune.metertronik.presentation.screen.daily_details

import DoubleBarChart
import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alcopoune.metertronik.presentation.components.MetricCard
import com.alcopoune.metertronik.presentation.components.PrimaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavController,
    id: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail: $id",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.scrim,
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DailySummary()
            AvgCard()
            PrimaryCard {
                Text("Power", color = MaterialTheme.colorScheme.scrim)
                DoubleBarChart()
            }
            HourlySectionScroll()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DailySummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrimaryCard(
            modifier = Modifier
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Money,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Rp 14.000",
                    color = MaterialTheme.colorScheme.scrim
                )
            }
        }

        PrimaryCard(
            modifier = Modifier
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.ElectricBolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "350 kWh",
                    color = MaterialTheme.colorScheme.scrim
                )

            }
        }
    }
}

@Composable
fun AvgCard() {
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

data class HourItem(
    val hour: Int
)

@Composable
fun HourlySectionScroll() {
    val hours = (0..23).map { HourItem(it) }

    // DEFAULT: jam paling awal (00:00)
    var selectedHour by remember { mutableStateOf(0) }

    Column {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(hours) { item ->
                HourCard(
                    hour = item.hour,
                    selectedHour = selectedHour,
                    onClick = {
                        selectedHour = item.hour
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        HourlyAverageCard()
        Spacer(modifier = Modifier.height(16.dp))
        DailySummary()

    }
}

@Composable
private fun HourlyAverageCard() {
    PrimaryCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ===== TITLE =====
            Text(
                text = "Average Metrics from 11:00",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                fontWeight = FontWeight.Medium
            )

            // ===== METRICS =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MetricItem(
                    title = "Current",
                    value = "12 A"
                )
                MetricItem(
                    title = "Power",
                    value = "12 W"
                )
                MetricItem(
                    title = "Voltage",
                    value = "12 V"
                )
            }


        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    icon: ImageVector = Icons.Outlined.TipsAndUpdates
) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.scrim,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim.copy(0.7f),
                fontWeight = FontWeight.Medium
            )
        }
}



@SuppressLint("DefaultLocale")
@Composable
fun HourCard(
    hour: Int,
    selectedHour: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedHour == hour

    Card(
        modifier = modifier
            .width(72.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
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
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = String.format("%02d:00", hour),
                fontWeight = FontWeight.Medium,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

