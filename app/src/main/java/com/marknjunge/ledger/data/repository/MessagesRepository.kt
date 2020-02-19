package com.marknjunge.ledger.data.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.marknjunge.ledger.data.local.MessagesDao
import com.marknjunge.ledger.data.local.SmsHelper
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.models.MpesaMessageEntity
import com.marknjunge.ledger.utils.DateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface MessagesRepository {
    suspend fun getMessages(): List<MpesaMessage>

    fun getPagedMessages(): LiveData<PagedList<MpesaMessage>>

    fun search(term: String): LiveData<PagedList<MpesaMessage>>

    suspend fun getMessagesGrouped(): List<MessageGroup>
}

class MessagesRepositoryImpl(
    private val smsHelper: SmsHelper,
    private val messagesDao: MessagesDao
) : MessagesRepository {

    override suspend fun getMessages(): List<MpesaMessage> {
        val messages = smsHelper.getMpesaMessages()

        val latest = messagesDao.getLatest()

        for (message in messages) {
            if (latest != null && latest.body == message.body) {
                break
            }

            try {
                val mpesaMessage = MpesaMessage.create(message.body)

                val entity = MpesaMessageEntity(
                    0,
                    mpesaMessage.code,
                    mpesaMessage.transactionType,
                    mpesaMessage.amount,
                    mpesaMessage.accountNumber,
                    mpesaMessage.transactionDate,
                    mpesaMessage.balance,
                    mpesaMessage.transactionCost,
                    mpesaMessage.body
                )

                messagesDao.insert(entity)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

        return messagesDao.getAll().map {
            MpesaMessage(
                it.body,
                it.code,
                it.transactionType,
                it.amount,
                it.accountNumber,
                it.transactionDate,
                it.balance,
                it.transactionCost
            )
        }
    }

    override fun getPagedMessages(): LiveData<PagedList<MpesaMessage>> {
        return messagesDao.pagedMessagesByDate().map {
            MpesaMessage(
                it.body,
                it.code,
                it.transactionType,
                it.amount,
                it.accountNumber,
                it.transactionDate,
                it.balance,
                it.transactionCost
            )
        }.toLiveData(30)
    }

    override fun search(term: String): LiveData<PagedList<MpesaMessage>> {
        return messagesDao.search("%$term%").map {
            MpesaMessage(
                it.body,
                it.code,
                it.transactionType,
                it.amount,
                it.accountNumber,
                it.transactionDate,
                it.balance,
                it.transactionCost
            )
        }.toLiveData(30)
    }

    override suspend fun getMessagesGrouped(): MutableList<MessageGroup> {
        return withContext(Dispatchers.IO) {
            groupByDate(getMessages())
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