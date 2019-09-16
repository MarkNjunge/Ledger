package com.marknkamau.ledger.di

import com.marknkamau.ledger.data.MessagesRepository
import com.marknkamau.ledger.data.MessagesRepositoryImpl
import com.marknkamau.ledger.data.SmsHelper
import com.marknkamau.ledger.ui.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SmsHelper(androidContext()) }
    single<MessagesRepository> { MessagesRepositoryImpl(get()) }
    viewModel { MainViewModel(get()) }
}