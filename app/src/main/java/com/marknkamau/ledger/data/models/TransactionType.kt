package com.marknkamau.ledger.data.models

enum class TransactionType(val positive: Boolean?) {
    REVERSAL(true),
    SEND(false),
    BUY(false),
    WITHDRAW(false),
    RECEIVE(true),
    AIRTIME(false),
    BALANCE(null),
    DEPOSIT(true),
    UNKNOWN(null)
}