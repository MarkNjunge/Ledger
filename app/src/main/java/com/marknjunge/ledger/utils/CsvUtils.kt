package com.marknjunge.ledger.utils

import com.marknjunge.ledger.data.models.MpesaMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CsvUtils {
    suspend fun generateCsv(messages: List<MpesaMessage>): List<String> =
        withContext(Dispatchers.IO) {
            val output = mutableListOf<String>()

            output.add("type,code,amount,transaction_cost,account_number,transaction_date,balance")
            messages.forEach {
                output.add(
                    "${it.transactionType.name},${it.code},${it.amount},${it.transactionCost},${it.accountNumber
                        ?: ""},${it.transactionDate},${it.balance}"
                )
            }

            output
        }
}