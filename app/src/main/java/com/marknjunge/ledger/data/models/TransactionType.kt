package com.marknjunge.ledger.data.models

import android.annotation.SuppressLint

enum class TransactionType(val positive: Boolean?) {
    REVERSAL(true),
    SEND(false),
    PAY_BILL(false),
    BUY_GOODS(false),
    WITHDRAW(false),
    RECEIVE(true),
    AIRTIME(false),
    AIRTIME_RECEIVE(null),
    BALANCE(null),
    DEPOSIT(true),
    UNKNOWN(null);

    @SuppressLint("DefaultLocale")
    fun string() = this.name.replace("_", " ").toLowerCase().split(" ").joinToString(" ") {it.capitalize()}
}