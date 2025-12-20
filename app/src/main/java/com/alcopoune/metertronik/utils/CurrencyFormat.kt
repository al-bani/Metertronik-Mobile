package com.alcopoune.metertronik.utils
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(value: Number): String {
    val localeID = Locale("in", "ID")
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    formatter.maximumFractionDigits = 0
    return formatter.format(value)
        .replace("\u00A0", " ")
}

