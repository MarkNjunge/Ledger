package com.marknjunge.ledger.data.models

enum class TransactionType(val positive: Boolean?) {
    REVERSAL(true),
    SEND(false),
    PAY_BILL(false),
    BUY_GOODS(false),
    WITHDRAW(false),
    RECEIVE(true),
    AIRTIME(false),
    BALANCE(null),
    DEPOSIT(true),
    UNKNOWN(null)
}