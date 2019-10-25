package com.marknjunge.ledger.di

import com.marknjunge.ledger.data.MessagesRepository
import com.marknjunge.ledger.data.MessagesRepositoryImpl
import com.marknjunge.ledger.data.SmsHelper
import com.marknjunge.ledger.ui.MainViewModel
import com.marknjunge.ledger.utils.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SmsHelper(androidContext()) }
    single<MessagesRepository> { MessagesRepositoryImpl(get()) }
    viewModel { MainViewModel(get()) }
}