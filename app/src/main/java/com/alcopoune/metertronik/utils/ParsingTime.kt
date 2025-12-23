package com.alcopoune.metertronik.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@RequiresApi(Build.VERSION_CODES.O)
fun String.toHour(): Int {
    return OffsetDateTime
        .parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .hour
}

@OptIn(ExperimentalTime::class)
@RequiresApi(Build.VERSION_CODES.O)
fun parseToLocalDate(date: String): LocalDate {
    return try {
        Instant.parse(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } catch (e: Exception) {
        LocalDate.parse(date.substring(0, 10))
    }
}

/**
 * DAILY label
 * Contoh: 2 Feb
 */
@RequiresApi(Build.VERSION_CODES.O)
fun formatDailyLabel(date: String): String {
    val localDate = parseToLocalDate(date)

    val day = localDate.dayOfMonth
    val month = localDate.month.getDisplayName(
        TextStyle.SHORT,
        Locale("id", "ID")
    )

    return "$day $month"
}

/**
 * MONTHLY label
 * Contoh: Okt 25
 */
@RequiresApi(Build.VERSION_CODES.O)
fun formatMonthlyLabel(date: String): String {
    val localDate = parseToLocalDate(date)

    val month = localDate.month.getDisplayName(
        TextStyle.SHORT,
        Locale("id", "ID")
    )

    val year = localDate.year % 100   // 2025 -> 25

    return "$month $year"
}

@RequiresApi(Build.VERSION_CODES.O)
fun daysBeforeEndOfMonth(
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val today = LocalDate.now(zoneId)

    val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

    val daysBetween = ChronoUnit.DAYS
        .between(today, endOfMonth)
        .toInt()

    val targetDay = endOfMonth.dayOfMonth
    val targetMonth = endOfMonth.month.getDisplayName(
        TextStyle.FULL,
        Locale.ENGLISH
    )

    return when {
        daysBetween > 0 ->
            "$daysBetween days before $targetDay $targetMonth"

        daysBetween == 0 ->
            "Today is $targetDay $targetMonth"

        else ->
            "${kotlin.math.abs(daysBetween)} days after $targetDay $targetMonth"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun progressToEndOfMonth(
    zoneId: ZoneId = ZoneId.systemDefault()
): Float {
    val today = LocalDate.now(zoneId)
    val endOfMonth = today.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())

    val totalDays = ChronoUnit.DAYS.between(
        today.withDayOfMonth(1),
        endOfMonth
    ).toFloat()

    val passedDays = ChronoUnit.DAYS.between(
        today.withDayOfMonth(1),
        today
    ).toFloat()

    return when {
        totalDays <= 0f -> 1f
        passedDays <= 0f -> 0f
        passedDays >= totalDays -> 1f
        else -> passedDays / totalDays
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(value: String): String {
    return runCatching {
        val date = Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
        date.format(formatter)
    }.getOrDefault(value)
}
