package com.marknjunge.ledger.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marknjunge.ledger.data.MessagesRepository
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.CsvUtils
import kotlinx.coroutines.launch

class MainViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _groupedMessages = MutableLiveData<List<MpesaMessage>>()
    val groupedMessages = _groupedMessages

    fun getMessages() {
        viewModelScope.launch {
            _loading.value = true
            _groupedMessages.value = messagesRepository.getMessages()
            _loading.value = false
        }
    }

    fun getMessagesForCsv(): LiveData<List<String>> {
        val livedata = MutableLiveData<List<String>>()

        viewModelScope.launch {
            _loading.value = true
            val messages = messagesRepository.getMessages()
            livedata.value = CsvUtils.generateCsv(messages)
            _loading.value = false
        }

        return livedata
    }
}