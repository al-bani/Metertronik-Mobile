//package com.alcopoune.metertronik.presentation.components.calendar
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ChevronLeft
//import androidx.compose.material.icons.filled.ChevronRight
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.YearMonth
//import java.time.format.TextStyle
//import java.util.Locale
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun CustomCalendar(
//    modifier: Modifier = Modifier,
//    onDateSelected: (LocalDate) -> Unit
//) {
//    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
//    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
//
//    val daysInMonth = remember(currentMonth) {
//        generateMonthDates(currentMonth)
//    }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        CalendarHeader(
//            currentMonth = currentMonth,
//            onPrevious = { currentMonth = currentMonth.minusMonths(1) },
//            onNext = { currentMonth = currentMonth.plusMonths(1) }
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        WeekDaysRow()
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(7),
//            modifier = Modifier.fillMaxWidth(),
//            userScrollEnabled = false
//        ) {
//            items(daysInMonth) { date ->
//                CalendarDayItem(
//                    date = date,
//                    isCurrentMonth = date.month == currentMonth.month,
//                    isToday = date == LocalDate.now(),
//                    isSelected = date == selectedDate,
//                    onClick = {
//                        selectedDate = date
//                        onDateSelected(date)
//                    }
//                )
//            }
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//private fun CalendarHeader(
//    currentMonth: YearMonth,
//    onPrevious: () -> Unit,
//    onNext: () -> Unit
//) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        IconButton(onClick = onPrevious) {
//            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
//        }
//
//        Text(
//            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//
//        IconButton(onClick = onNext) {
//            Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//private fun WeekDaysRow() {
//    val daysOfWeek = DayOfWeek.values()
//
//    Row(modifier = Modifier.fillMaxWidth()) {
//        daysOfWeek.forEach { day ->
//            Text(
//                text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
//                modifier = Modifier.weight(1f),
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.primary,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}
//
//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//private fun CalendarDayItem(
//    date: LocalDate,
//    isCurrentMonth: Boolean,
//    isToday: Boolean,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    val backgroundColor = when {
//        isSelected -> MaterialTheme.colorScheme.primary
//        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
//        else -> Color.Transparent
//    }
//
//    val textColor = when {
//        isSelected -> Color.White
//        !isCurrentMonth -> Color.Gray
//        else -> MaterialTheme.colorScheme.onSurface
//    }
//
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f)
//            .padding(4.dp)
//            .clip(CircleShape)
//            .background(backgroundColor)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            text = date.dayOfMonth.toString(),
//            color = textColor,
//            style = MaterialTheme.typography.bodyMedium
//        )
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//private fun generateMonthDates(month: YearMonth): List<LocalDate> {
//    val firstDayOfMonth = month.atDay(1)
//    val lastDayOfMonth = month.atEndOfMonth()
//
//    val startDay = firstDayOfMonth.minusDays(
//        ((firstDayOfMonth.dayOfWeek.value % 7).toLong())
//    )
//
//    val endDay = lastDayOfMonth.plusDays(
//        (6 - (lastDayOfMonth.dayOfWeek.value % 7)).toLong()
//    )
//
//    val dates = mutableListOf<LocalDate>()
//    var current = startDay
//
//    while (!current.isAfter(endDay)) {
//        dates.add(current)
//        current = current.plusDays(1)
//    }
//
//    return dates
//}
