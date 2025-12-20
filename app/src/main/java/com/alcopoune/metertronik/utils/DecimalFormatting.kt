package com.alcopoune.metertronik.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


fun DecimalFormater(value: Number): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("0.0", symbols)
    return formatter.format(value)
}