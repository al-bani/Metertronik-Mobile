package com.alcopoune.metertronik.presentation.components.calendar


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarRangePicker(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onRangeSelected: (LocalDate?, LocalDate?) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val dates = remember(currentMonth) {
        generateMonthDates(currentMonth)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CalendarHeader(
            currentMonth = currentMonth,
            onPrevious = { currentMonth = currentMonth.minusMonths(1) },
            onNext = { currentMonth = currentMonth.plusMonths(1) }
        )

        Spacer(Modifier.height(12.dp))
        WeekDaysRow()
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false
        ) {
            items(dates) { date ->
                CalendarRangeDayItem(
                    date = date,
                    isCurrentMonth = date.month == currentMonth.month,
                    startDate = startDate,
                    endDate = endDate,
                    onClick = {
                        when {
                            startDate == null -> {
                                onRangeSelected(date, null)
                            }
                            endDate == null && date >= startDate -> {
                                onRangeSelected(startDate, date)
                            }
                            else -> {
                                onRangeSelected(date, null)
                            }
                        }
                    }
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous Month",
                tint = MaterialTheme.colorScheme.scrim
                )
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.scrim.copy(0.8f)
        )

        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = MaterialTheme.colorScheme.scrim)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekDaysRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        DayOfWeek.values().forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = MaterialTheme.colorScheme.scrim,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CalendarRangeDayItem(
    date: LocalDate,
    isCurrentMonth: Boolean,
    startDate: LocalDate?,
    endDate: LocalDate?,
    onClick: () -> Unit
) {
    val isStart = date == startDate
    val isEnd = date == endDate
    val isInRange =
        startDate != null && endDate != null &&
                date.isAfter(startDate) && date.isBefore(endDate)

    val backgroundColor = when {
        isStart || isEnd -> MaterialTheme.colorScheme.onSecondary
        isInRange -> MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.4f)
        else -> Color.Transparent
    }

    val textColor = when {
        isStart || isEnd -> MaterialTheme.colorScheme.background
        !isCurrentMonth -> MaterialTheme.colorScheme.scrim.copy(0.4f)
        isInRange -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.scrim.copy(0.8f)
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun generateMonthDates(month: YearMonth): List<LocalDate> {
    val firstDay = month.atDay(1)
    val lastDay = month.atEndOfMonth()

    val start = firstDay.minusDays((firstDay.dayOfWeek.value % 7).toLong())
    val end = lastDay.plusDays((6 - (lastDay.dayOfWeek.value % 7)).toLong())

    val result = mutableListOf<LocalDate>()
    var current = start

    while (!current.isAfter(end)) {
        result.add(current)
        current = current.plusDays(1)
    }

    return result
}
