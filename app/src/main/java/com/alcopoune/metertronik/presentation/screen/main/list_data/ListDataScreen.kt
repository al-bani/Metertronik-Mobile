package com.alcopoune.metertronik.presentation.screen.main.list_data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel
import com.alcopoune.metertronik.presentation.components.calendar.CalendarRangePicker
import com.alcopoune.metertronik.presentation.navigation.MainBottomBar
import com.alcopoune.metertronik.presentation.navigation.Routes
import com.alcopoune.metertronik.presentation.components.loading.RealtimePulse
import com.alcopoune.metertronik.presentation.components.loading.ShimmerListData
import com.alcopoune.metertronik.domain.model.DailyData
import com.alcopoune.metertronik.utils.DecimalFormater
import com.alcopoune.metertronik.utils.formatDate
import com.alcopoune.metertronik.utils.formatRupiah
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//data class HistoryItemUi(
//    val id: String,
//    val deviceId: String,
//    val dateLabel: String,
//    val costLabel: String,
//    val energyLabel: String
//)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDataScreen(
    navController: NavHostController,
    viewModel: ListDataViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    val filterSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val dateRangeSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var currentSortBy by remember { mutableStateOf("Time") }
    // Default urutan awal selaras dengan ViewModel (TIME + DESC)
    var currentOrder by remember { mutableStateOf("DESC") }
    var showDateRangeSheet by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val storedDeviceId by viewModel.deviceId.collectAsState(initial = null)
    val listState = rememberLazyListState()

    LaunchedEffect(storedDeviceId) {
        val deviceId = storedDeviceId?.takeIf { it.isNotBlank() } ?: return@LaunchedEffect
        viewModel.load(deviceId)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { index ->
                val success = uiState as? ListDataState.Success
                if (success != null && success.hasMore && index != null && index >= success.data.size - 2) {
                    viewModel.loadMore()
                }
            }
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

            when (uiState) {
                ListDataState.Loading -> {
                    ShimmerListData()
                }

                is ListDataState.Error -> {
                    val errorState = uiState as ListDataState.Error
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorState.message,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val deviceId = storedDeviceId?.takeIf { it.isNotBlank() } ?: return@Button
                                viewModel.load(deviceId)
                            }
                        ) {
                            Text("Try Again")
                        }
                    }
                }

                is ListDataState.Success -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            enabled = false,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp, max = 52.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            showDateRangeSheet = true
                                        }
                                    )
                                },
                            placeholder = {Text(
                                text = "Select date range",
                                color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                            )},
                            trailingIcon = {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.scrim.copy(0.8f))
                            },
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

                    Spacer(modifier = Modifier.height(16.dp))
                    RealtimePulse(modifier = Modifier.fillMaxWidth()){
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navController.navigate(Routes.Detail.createRoute(""))
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ElectricBolt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Today Data",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Filled.GraphicEq,
                                    contentDescription = "More options",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val successState = uiState as ListDataState.Success
                    if (successState.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Data Found", style = MaterialTheme.typography.titleMedium)
                        }
                    } else {

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {


                            itemsIndexed(
                                items = successState.data,
                                key = { _, item -> item.day }
                            ) { index, item ->

                                val isUp = if (index == (successState.data.size-1)) {
                                    true
                                } else {
                                    val previousItem = successState.data[index + 1]
                                    item.energy > previousItem.energy
                                }

                                HistoryItemRow(
                                    item = item,
                                    onClick = {
                                        navController.navigate(Routes.Detail.createRoute(item.day))
                                    },
                                    isUp = isUp
                                )
                            }

                            if (successState.isLoadingMore) {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        FilterBottomSheet(
            sheetState = filterSheetState,
            initialSortBy = currentSortBy,
            initialOrder = currentOrder,
            onApply = { sortBy, order ->
                currentSortBy = sortBy
                currentOrder = order
                viewModel.updateSort(sortBy, order)
                showSheet = false
            },
            onDismiss = { showSheet = false }
        )
    }

    if (showDateRangeSheet){
        DateRangeSheet(
            sheetState = dateRangeSheetState,
            onDismiss = {showDateRangeSheet = false},
            onApplyRange = { start, end ->
                val displayFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                searchQuery = if (start != null && end != null) {
                    "${start.format(displayFormatter)} - ${end.format(displayFormatter)}"
                } else {
                    ""
                }
                viewModel.applyDateRange(start, end)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HistoryItemRow(
    item: DailyData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isUp : Boolean,
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
                tint = if (isUp) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.error,
                modifier = Modifier.rotate(if (isUp) 0f else 180f).size(32.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatDate(item.day),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatRupiah(item.totalCost)} - ${DecimalFormater(item.energy)} kWh",
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onApplyRange: (LocalDate?, LocalDate?) -> Unit
){
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    // ✅ derived state (JANGAN pakai remember mutable)
    val isApplyEnabled = startDate != null && endDate != null

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        sheetMaxWidth = 400.dp,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {

            CalendarRangePicker(
                startDate = startDate,
                endDate = endDate,
                onRangeSelected = { start, end ->
                    startDate = start
                    endDate = end
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,

                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = startDate?.let { formatDate(it.toString()) } ?: "Select start date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.scrim
                    )
                    Text(
                        text = "Start Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = endDate?.let { formatDate(it.toString()) } ?: "Select end date",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.scrim
                    )
                    Text(
                        text = "End Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.scrim.copy(0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),

                    onClick = {
                        startDate = null
                        endDate = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.titleSmall,

                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = isApplyEnabled,
                    onClick = {
                        onApplyRange(startDate, endDate)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Apply",
                        style = MaterialTheme.typography.titleSmall,

                    )
                }
            }
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
    var lastSortBy by remember { mutableStateOf(initialSortBy) }

    // Reset order hanya ketika user benar‑benar mengubah sortBy,
    // tapi tetap menghormati nilai awal (initialOrder) saat sheet pertama kali dibuka.
    LaunchedEffect(tempSortBy) {
        if (tempSortBy != lastSortBy) {
            tempOrder = if (tempSortBy == "Time") "DESC" else "ASC"
            lastSortBy = tempSortBy
        }
    }

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

            // ===== Title =====
            Text(
                text = "Filter Data",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.scrim
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ===== SORT BY =====
            Text(
                text = "Sort By",
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
                        icon = if (item == "Cost")
                            Icons.Default.Money
                        else
                            Icons.Default.DateRange
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ===== ORDER =====
            Text(
                text = "Sort",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.scrim
            )

            val orders = if (tempSortBy == "Time") {
                listOf(
                    "Oldest" to "ASC",
                    "Newer" to "DESC"
                )
            } else {
                listOf(
                    "Lowest" to "ASC",
                    "Highest" to "DESC"
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                orders.forEach { (label, value) ->
                    SortButton(
                        item = label,
                        selectedItem = if (tempOrder == value) label else "",
                        onClick = { tempOrder = value },
                        modifier = Modifier.weight(1f),
                        icon = if (value == "ASC")
                            Icons.Default.ArrowUpward
                        else
                            Icons.Default.ArrowDownward
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===== APPLY =====
            Button(
                onClick = {
                    onApply(tempSortBy, tempOrder)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            ) {
                Text("Apply Filter")
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

