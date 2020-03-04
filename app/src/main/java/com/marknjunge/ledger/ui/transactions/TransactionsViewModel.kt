package com.marknjunge.ledger.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.utils.CsvUtils
import kotlinx.coroutines.launch

class TransactionsViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {

    fun getPagedMessages(): LiveData<PagedList<MpesaMessage>> {
        return messagesRepository.getPagedMessages()
    }

    fun search(term: String): LiveData<PagedList<MpesaMessage>> {
        return messagesRepository.search(term)
    }

    fun getMessagesForExport(): LiveData<List<String>> {
        val liveData = MutableLiveData<List<String>>()
        viewModelScope.launch {
            val messages = messagesRepository.getMessages()
            liveData.value = CsvUtils.generateCsvLines(messages)
        }

        return liveData
    }
}
