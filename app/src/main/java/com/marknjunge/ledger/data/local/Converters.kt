package com.marknjunge.ledger.data.local

import androidx.room.TypeConverter
import com.marknjunge.ledger.data.models.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(transactionType: TransactionType): String {
        return transactionType.name
    }

    @TypeConverter
    fun fromString(string: String): TransactionType {
        return TransactionType.valueOf(string)
    }
}
