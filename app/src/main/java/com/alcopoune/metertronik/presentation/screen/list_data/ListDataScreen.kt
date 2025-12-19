package com.alcopoune.metertronik.presentation.screen.list_data

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alcopoune.metertronik.presentation.navigation.MainBottomBar
import com.alcopoune.metertronik.presentation.navigation.Routes

data class HistoryItemUi(
    val id: String,
    val dateLabel: String,
    val costLabel: String,
    val energyLabel: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDataScreen(
    navController: NavHostController
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var currentSortBy by remember { mutableStateOf("Time") }
    var currentOrder by remember { mutableStateOf("ASC") }

    val items = remember {
        List(10) {
            HistoryItemUi(
                id = it.toString(),
                dateLabel = "12 Des 2025",
                costLabel = "Rp ${25_000 + it * 1000}",
                energyLabel = "${3.0 + it * 0.1} kWh"
            )
        }
    }

    // Sorting logic
    val sortedItems = remember(items, currentSortBy, currentOrder) {
        items.sortedWith(compareBy {
            when (currentSortBy) {
                "Biaya" -> it.costLabel.filter { c -> c.isDigit() }.toInt()
                "Energy" -> it.energyLabel.filter { c -> c.isDigit() || c == '.' }.toFloat()
                else -> 0
            }
        }).let { if (currentOrder == "DESC") it.reversed() else it }
    }

    Scaffold(
        bottomBar = { MainBottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    enabled = false,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 40.dp, max = 48.dp),
                    label = { Text("Cari history") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledTextColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    ),
                    singleLine = true
                )
                IconButton(
                    onClick = { showSheet = true },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Tune, contentDescription = "Filter")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedItems) { item ->
                    HistoryItemRow(
                        item = item,
                        onClick = {
                            navController.navigate(Routes.Detail.createRoute("device-001"))
                        }
                    )
                }
            }
        }
    }

    if (showSheet) {
        FilterBottomSheet(
            sheetState = sheetState,
            initialSortBy = currentSortBy,
            initialOrder = currentOrder,
            onApply = { sortBy, order ->
                currentSortBy = sortBy
                currentOrder = order
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }
}

@Composable
private fun HistoryItemRow(
    item: HistoryItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Navigation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.dateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.costLabel} - ${item.energyLabel}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.scrim
                )
            }

            Icon(
                imageVector = Icons.Filled.MoreHoriz,
                contentDescription = "More options",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    sheetState: SheetState,
    initialSortBy: String,
    initialOrder: String,
    onApply: (sortBy: String, order: String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSortBy by remember { mutableStateOf(initialSortBy) }
    var tempOrder by remember { mutableStateOf(initialOrder) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = Color(0xFFF7F7F7)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Filter & Urutkan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Urutkan Berdasarkan",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.scrim
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                listOf("Cost", "Time").forEach { item ->
                    SortButton(
                        item = item,
                        selectedItem = tempSortBy,
                        onClick = { tempSortBy = it },
                        modifier = Modifier.weight(1f),
                        icon = if (item == "Cost") Icons.Default.Money else Icons.Default.DateRange
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Urutan",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.scrim
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val orders = listOf("Terendah" to "ASC", "Tertinggi" to "DESC")
                orders.forEach { (label, value) ->
                    SortButton(
                        item = label,
                        selectedItem = if (tempOrder == value) label else "",
                        onClick = { tempOrder = value },
                        modifier = Modifier.weight(1f),
                        icon = if (value == "ASC") Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onApply(tempSortBy, tempOrder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Text("Terapkan Filter", color = Color.White)
            }
        }
    }
}

@Composable
fun SortButton(
    item: String,
    selectedItem: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null, // optional icon
) {
    val isSelected = selectedItem == item

    OutlinedButton(
        onClick = { onClick(item) },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
            contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.scrim
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.scrim else MaterialTheme.colorScheme.scrim),
        modifier = modifier.height(48.dp)
    ) {

        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
        }

        Text(item, fontWeight = FontWeight.SemiBold)
    }
}

