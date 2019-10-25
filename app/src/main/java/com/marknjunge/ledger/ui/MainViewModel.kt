package com.marknjunge.ledger.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marknjunge.ledger.data.MessagesRepository
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.utils.CsvUtils
import kotlinx.coroutines.launch

class MainViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _groupedMessages = MutableLiveData<List<MessageGroup>>()
    val groupedMessages = _groupedMessages

    fun getMessages() {
        viewModelScope.launch {
            _loading.value = true
            _groupedMessages.value = messagesRepository.getMessagesGrouped()
            _loading.value = false
        }
    }

    fun getMessagesForCsv(): LiveData<List<String>> {
        val livedata = MutableLiveData<List<String>>()

        viewModelScope.launch {
            _loading.value = true
            val messages = messagesRepository.getMessages()
            val csvContent = CsvUtils.generateCsv(messages)
            livedata.value = csvContent
//            if (storage.isExternal) {
//                storage.createFolder("ledger")
//                storage.writeToFile("ledger/transactions.csv", output.joinToString("\n"))
//            } else {
//                storage.writeToFile("transactions.csv", output.joinToString("\n"))
//            }
            _loading.value = false
        }

        return livedata
    }
}