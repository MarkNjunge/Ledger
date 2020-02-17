package com.marknjunge.ledger.data.models

import androidx.room.*

@Entity(tableName = "mpesa_messages", indices = [Index(value = ["code"], unique = true)])
data class MpesaMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "transaction_type")
    val transactionType: TransactionType,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "account_number")
    val accountNumber: String?,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: Long,

    @ColumnInfo(name = "balance")
    val balance: Double,

    @ColumnInfo(name = "transaction_cost")
    val transactionCost: Double,

    @ColumnInfo(name = "body")
    val body: String
)