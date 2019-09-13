package com.marknkamau.ledger.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

data class DateTime(val year: Int,
                    val month: Int,
                    val dayOfMonth: Int,
                    val hourOfDay: Int,
                    val minute: Int,
                    val second: Int = 0,
                    val millisecond: Int = 0) {

    companion object {
        val now: DateTime
            get() = System.currentTimeMillis().toDateTime()

        private fun Long.toDateTime() = Date(this).toDateTime()

        fun fromTimestamp(timestamp: Long): DateTime = Date(timestamp).toDateTime()
        fun fromUnix(timestamp: Long): DateTime = Date(timestamp * 1000).toDateTime()
    }

    val unix: Long
        get() {
            val now = Calendar.getInstance()
            // Month starts at 0
            now.set(this.year, this.month - 1, this.dayOfMonth, this.hourOfDay, this.minute, this.second)
            return now.time.time / 1000L
        }

    val timestamp: Long
        get() {
            val now = Calendar.getInstance()
            // Month starts at 0
            now.set(this.year, this.month - 1, this.dayOfMonth, this.hourOfDay, this.minute, this.second)
            now.set(Calendar.MILLISECOND, this.millisecond)
            return now.time.time
        }

    fun format(format: String): String {
        val now = Calendar.getInstance()
        // Month starts at 0
        now.set(this.year, this.month - 1, this.dayOfMonth, this.hourOfDay, this.minute, this.second)
        return now.time.format(format)
    }

}

fun Date.toDateTime(): DateTime {
    val hourOfDay = this.format("H").toInt() // Format according to 24Hr from 0-23
    val minute = this.format("m").toInt()
    val year = this.format("yyyy").toInt()
    val month = this.format("M").toInt()
    val dayOfMonth = this.format("dd").toInt()
    val second = this.format("s").toInt()
    val millisecond = this.format("S").toInt()

    return DateTime(year, month, dayOfMonth, hourOfDay, minute, second, millisecond)
}

@SuppressLint("SimpleDateFormat")
fun Date.format(pattern: String): String = SimpleDateFormat(pattern).format(this)
