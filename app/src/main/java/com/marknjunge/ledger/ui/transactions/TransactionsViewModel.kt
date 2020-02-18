package com.marknjunge.ledger.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.repository.MessagesRepository

class TransactionsViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {

    fun getPagedMessages(): LiveData<PagedList<MpesaMessage>> {
        return messagesRepository.getPagedMessages()
    }
}