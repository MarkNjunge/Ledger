package com.marknjunge.ledger.di

import androidx.room.Room
import com.marknjunge.ledger.data.local.AppDatabase
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.data.repository.MessagesRepositoryImpl
import com.marknjunge.ledger.data.local.SmsHelper
import com.marknjunge.ledger.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SmsHelper(androidContext()) }

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "ledger-db").build() }
    single { get<AppDatabase>().messagesDao() }

    single<MessagesRepository> { MessagesRepositoryImpl(get(), get()) }

    viewModel { MainViewModel(get()) }
}