package com.marknkamau.ledger.data

import android.annotation.SuppressLint
import com.marknkamau.ledger.data.models.MessageGroup
import com.marknkamau.ledger.data.models.MpesaMessage
import com.marknkamau.ledger.utils.DateTime

interface MessagesRepository {
    suspend fun getMessages(): MutableList<MpesaMessage>

    suspend fun getMessagesGrouped(): MutableList<MessageGroup>
}

class MessagesRepositoryImpl(private val smsHelper: SmsHelper) : MessagesRepository {
    override suspend fun getMessages(): MutableList<MpesaMessage> {
        return smsHelper.getMpesaMessages()
    }

    override suspend fun getMessagesGrouped(): MutableList<MessageGroup> {
        return groupByDate(getMessages())
    }

    @SuppressLint("UseSparseArrays")
    private fun groupByDate(messages: List<MpesaMessage>): MutableList<MessageGroup> {
        val treeMap = HashMap<Long, MutableList<MpesaMessage>>()

        messages.forEach {
            // Reduce the date's accuracy to only up to the day of the month
            val dateTime = DateTime.fromTimestamp(it.date)
            val bigDateTime =
                DateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, 0, 0, 0, 0)
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