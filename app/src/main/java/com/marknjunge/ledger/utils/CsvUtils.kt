package com.marknjunge.ledger.utils

import com.marknjunge.ledger.data.models.MpesaMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvUtils {
    suspend fun generateCsv(messages: List<MpesaMessage>): List<String> =
        withContext(Dispatchers.IO) {
            val output = mutableListOf<String>()

            output.add("type,code,amount,account_number,transaction_date")
            messages.forEach {
                output.add("${it.type.name},${it.code},${it.amount},${it.accountNumber},${it.transactionDate}")
            }

            output
        }
}