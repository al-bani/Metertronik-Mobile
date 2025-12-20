package com.alcopoune.metertronik.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun String.toHour(): Int {
    return OffsetDateTime
        .parse(this, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        .hour
}