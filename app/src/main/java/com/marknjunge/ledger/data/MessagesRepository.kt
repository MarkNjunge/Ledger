package com.marknjunge.ledger.data

import android.annotation.SuppressLint
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.DateTime
import com.marknjunge.ledger.utils.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MessagesRepository {
    suspend fun getMessages(): List<MpesaMessage>

    suspend fun getMessagesGrouped(): List<MessageGroup>

    suspend fun exportAsCSV()
}

class MessagesRepositoryImpl(
    private val smsHelper: SmsHelper,
    private val storage: LocalStorage
) : MessagesRepository {

    override suspend fun getMessages(): List<MpesaMessage> {
        return smsHelper.getMpesaMessages()
    }

    override suspend fun getMessagesGrouped(): MutableList<MessageGroup> {
        return withContext(Dispatchers.IO) {
            groupByDate(getMessages())
        }
    }

    override suspend fun exportAsCSV() {
        withContext(Dispatchers.IO) {
            val messages = smsHelper.getMpesaMessages()

            val output = mutableListOf<String>()

            output.add("type,code,amount,account_number,transaction_date")
            messages.forEach {
                output.add("${it.type.name},${it.code},${it.amount},${it.accountNumber},${it.transactionDate}")
            }

            if (storage.isExternal) {
                storage.createFolder("ledger")
                storage.writeToFile("ledger/transactions.csv", output.joinToString("\n"))
            } else {
                storage.writeToFile("transactions.csv", output.joinToString("\n"))
            }
        }
    }

    @SuppressLint("UseSparseArrays")
    private fun groupByDate(messages: List<MpesaMessage>): MutableList<MessageGroup> {
        val treeMap = HashMap<Long, MutableList<MpesaMessage>>()

        messages.forEach {
            // Reduce the date's accuracy to only up to the day of the month
            val dateTime = DateTime.fromTimestamp(it.transactionDate)
            val bigDateTime =
                DateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, 0, 0, 0)
            val date = bigDateTime.timestamp

            // If the a messages does not exist for the date, create it
            if (treeMap[date] == null) {
                treeMap[date] = mutableListOf()
            }

            // Add the message to the messages
            treeMap[date]!!.add(it)
        }

        // Create a list of messages from the map
        val list = mutableListOf<MessageGroup>()
        for (key in treeMap.keys) {
            list.add(MessageGroup(key, treeMap[key]!!))
        }

        return list.sortedBy { it.date }.reversed().toMutableList()
    }

}