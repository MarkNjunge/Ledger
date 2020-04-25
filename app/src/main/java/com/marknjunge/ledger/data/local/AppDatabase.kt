package com.marknjunge.ledger.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.marknjunge.ledger.data.models.MpesaMessageEntity

@Database(entities = [MpesaMessageEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
}