package com.marknjunge.ledger.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.marknjunge.ledger.data.local.MessagesDao
import com.marknjunge.ledger.data.local.SmsHelper
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.models.exception.MessageParseException
import com.marknjunge.ledger.data.models.toMessages
import com.marknjunge.ledger.utils.DateTime
import timber.log.Timber

interface MessagesRepository {
    suspend fun fetchMessages()

    suspend fun getMessages(): List<MpesaMessage>

    suspend fun getMessagesGroupedByDate(): List<MessageGroup>

    suspend fun getFilteredMessages(query: String, params: Array<String>): List<MpesaMessage>

    fun getMessagesPaged(): LiveData<PagedList<MpesaMessage>>

    fun getFilteredMessagesPaged(query: String, params: Array<String>): LiveData<PagedList<MpesaMessage>>

    fun searchMessagesPaged(term: String): LiveData<PagedList<MpesaMessage>>
}

class MessagesRepositoryImpl(
        private val smsHelper: SmsHelper,
        private val messagesDao: MessagesDao
) : MessagesRepository {

    override suspend fun fetchMessages() {
        val latest = messagesDao.getLatestMessage()

        var messages = smsHelper.getMpesaMessages()
        if (latest != null) {
            // If there are already saved messages, sort the messages by date descending so that the last saved
            // one is arrived at quicker
            messages = messages.sortedByDescending { it.date }
        }

        val newMessages = mutableListOf<MpesaMessage>()
        for (message in messages) {
            if (latest != null && latest.body == message.body) {
                break
            }

            try {
                newMessages.add(MpesaMessage.create(message.body))
            } catch (e: Exception) {
                if (e is MessageParseException) {
                    FirebaseCrashlytics.getInstance().log(e.body)
                }
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        messagesDao.insertAll(newMessages.reversed().map { it.toEntity() })
        Timber.d("Inserted ${newMessages.size} new messages")
    }

    override suspend fun getMessages(): List<MpesaMessage> {
        return messagesDao.getMessages().toMessages()
    }

    override suspend fun getMessagesGroupedByDate(): MutableList<MessageGroup> {
        return groupByDate(getMessages())
    }

    override suspend fun getFilteredMessages(query: String, params: Array<String>): List<MpesaMessage> {
        val q = SimpleSQLiteQuery(query, params)
        return messagesDao.filter(q).toMessages()
    }

    override fun getMessagesPaged(): LiveData<PagedList<MpesaMessage>> {
        return messagesDao.getMessagesPaged().toMessages().toLiveData(30)
    }

    override fun getFilteredMessagesPaged(query: String, params: Array<String>): LiveData<PagedList<MpesaMessage>> {
        val q = SimpleSQLiteQuery(query, params)
        return messagesDao.filterPaged(q).toMessages().toLiveData(30)
    }

    override fun searchMessagesPaged(term: String): LiveData<PagedList<MpesaMessage>> {
        return messagesDao.searchPaged("%$term%").toMessages().toLiveData(30)
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

            // If the a groupedMessages does not exist for the date, create it
            if (treeMap[date] == null) {
                treeMap[date] = mutableListOf()
            }

            // Add the message to the groupedMessages
            treeMap[date]!!.add(it)
        }

        // Create a list of groupedMessages from the map
        val list = mutableListOf<MessageGroup>()
        for (key in treeMap.keys) {
            list.add(MessageGroup(key, treeMap[key]!!))
        }

        return list.sortedBy { it.date }.reversed().toMutableList()
    }

}