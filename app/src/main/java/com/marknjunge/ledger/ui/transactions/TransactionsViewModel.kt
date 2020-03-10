package com.marknjunge.ledger.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.utils.CsvUtils
import com.marknjunge.ledger.utils.DateTime
import kotlinx.coroutines.launch

class TransactionsViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {

    private var filter:Pair<String, Array<String>>? = null

    fun getPagedMessages(): LiveData<PagedList<MpesaMessage>> {
        return messagesRepository.getPagedMessages()
    }

    fun filter(search: String, startDate: DateTime?, endDate: DateTime?): LiveData<PagedList<MpesaMessage>> {
        return if (search.isBlank() && startDate == null && endDate == null) {
            filter = null
            messagesRepository.getPagedMessages()
        } else {
            val query = generateQuery(search, startDate, endDate)
            filter = query
            messagesRepository.getFilteredMessagesPaged(query.first, query.second)
        }
    }

    fun getMessagesForExport(): LiveData<List<String>> {
        val liveData = MutableLiveData<List<String>>()
        viewModelScope.launch {
            if (filter == null) {
                val messages = messagesRepository.getMessages()
                liveData.value = CsvUtils.generateCsvLines(messages)
            } else {
                val messages = messagesRepository.getFilteredMessages(filter!!.first, filter!!.second)
                liveData.value = CsvUtils.generateCsvLines(messages)
            }
        }

        return liveData
    }

    private fun generateQuery(search: String, startDate: DateTime?, endDate: DateTime?): Pair<String, Array<String>> {
        val params = mutableListOf<String>()

        var query = "SELECT * FROM mpesa_messages "
        var requiresWhere = true
        var requiresAnd = false

        if (search.isNotEmpty()) {
            if (requiresWhere) {
                query += " WHERE "
                requiresWhere = false
            }

            query += "body LIKE ?"
            params.add("%$search%")
            requiresAnd = true
        }

        startDate?.let {
            if (requiresWhere) {
                query += " WHERE "
                requiresWhere = false
            }
            if (requiresAnd) query += " AND "

            query += "transaction_date > ?"
            params.add(it.timestamp.toString())
            requiresAnd = true
        }

        endDate?.let {
            if (requiresWhere) {
                query += " WHERE "
                requiresWhere = false
            }
            if (requiresAnd) query += " AND "

            query += "transaction_date < ?"
            params.add(it.timestamp.toString())
            requiresAnd = true
        }

        query += " ORDER BY transaction_date DESC"
        return Pair(query, params.toTypedArray());
    }
}
