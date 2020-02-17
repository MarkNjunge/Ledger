package com.marknjunge.ledger.utils

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

object CurrencyFormatter {
    fun format(number: Double): String {
        val numberFormat = NumberFormat.getInstance(Locale("en"))
        numberFormat.roundingMode = RoundingMode.HALF_UP
        numberFormat.maximumFractionDigits = 2
        numberFormat.minimumFractionDigits = 2
        return "Ksh ${numberFormat.format(number)}"
    }
}