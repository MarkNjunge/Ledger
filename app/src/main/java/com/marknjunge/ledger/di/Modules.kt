package com.marknjunge.ledger.di

import androidx.room.Room
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppDatabase
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.data.local.AppPreferencesImpl
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.data.repository.MessagesRepositoryImpl
import com.marknjunge.ledger.data.local.SmsHelper
import com.marknjunge.ledger.ui.main.MainViewModel
import com.marknjunge.ledger.ui.transactions.TransactionsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { SmsHelper(androidContext()) }
    single<AppPreferences> { AppPreferencesImpl(androidContext()) }
    single {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig
    }

    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "ledger-db").build() }
    single { get<AppDatabase>().messagesDao() }

    single<MessagesRepository> { MessagesRepositoryImpl(get(), get()) }

    viewModel { MainViewModel(get()) }
    viewModel { TransactionsViewModel(get()) }
}