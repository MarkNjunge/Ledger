package com.marknjunge.ledger.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.data.models.MpesaMessage
import kotlinx.coroutines.launch

class MainViewModel(private val messagesRepository: MessagesRepository) : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _groupedMessages = MutableLiveData<List<MpesaMessage>>()
    val groupedMessages = _groupedMessages

    fun getMessages() {
        viewModelScope.launch {
            _loading.value = true
            messagesRepository.fetchMessages()
            _groupedMessages.value = messagesRepository.getMessages()
            _loading.value = false
        }
    }
}