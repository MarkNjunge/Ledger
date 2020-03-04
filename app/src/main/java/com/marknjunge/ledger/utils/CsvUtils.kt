package com.marknjunge.ledger.utils

import com.marknjunge.ledger.data.models.MpesaMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvUtils {
    suspend fun generateCsvLines(messages: List<MpesaMessage>): List<String> =
        withContext(Dispatchers.IO) {
            val output = mutableListOf<String>()

            output.add("type,code,amount,transaction_cost,account_number,transaction_date,balance")
            messages.forEach {
                it.apply {
                    val formattedDate = DateTime.fromTimestamp(transactionDate).format("yyyy-MM-dd HH:mm")
                    output.add(
                        "${transactionType.name},${code},${amount},${transactionCost},${accountNumber
                            ?: ""},${formattedDate},${balance}"
                    )
                }
            }

            output
        }
}