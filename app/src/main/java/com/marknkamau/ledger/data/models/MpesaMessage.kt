package com.marknkamau.ledger.data.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.marknkamau.ledger.utils.DateTime
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */
@Parcelize
data class MpesaMessage(val body: String) : Parcelable {
    @IgnoredOnParcel
    val code = body.split(Regex("( [Cc]onfirmed)"))[0].reversed().split(" ")[0].reversed()

    @IgnoredOnParcel
    @SuppressLint("DefaultLocale")
    val bodyLowerCase = body.toLowerCase()

    val type: TransactionType
        get() {
            return when {
                bodyLowerCase.contains(Regex("(.*) reversal (.*)")) -> TransactionType.REVERSAL
                bodyLowerCase.contains(Regex("(.*) sent to (.*) for account (.*)")) -> TransactionType.PAY_BILL
                bodyLowerCase.contains(Regex("(.*) paid to")) -> TransactionType.BUY_GOODS
                bodyLowerCase.contains(Regex("(.*) sent to (.*)")) -> TransactionType.SEND
                bodyLowerCase.contains(Regex("(.*)withdraw (.*)")) -> TransactionType.WITHDRAW
                bodyLowerCase.contains(Regex("(.*) received (.*)")) -> TransactionType.RECEIVE
                bodyLowerCase.contains(Regex("(.*) airtime (.*)")) -> TransactionType.AIRTIME
                bodyLowerCase.contains(Regex("(.*)your m-pesa balance (.*)")) -> TransactionType.BALANCE
                bodyLowerCase.contains(Regex("(.*) give (.*)")) -> TransactionType.DEPOSIT
                else -> TransactionType.UNKNOWN
            }
        }

    val amount: Double
        get() {
            return body.split("Ksh")[1].split(" ")[0].replace(",", "").toDouble()
        }

    val accountNumber: String?
        get() {
            return when (type) {
                TransactionType.REVERSAL -> null
                TransactionType.SEND -> bodyLowerCase.split("to ")[1].split(" on")[0]
                TransactionType.PAY_BILL -> bodyLowerCase.split("to ")[1].split(" on")[0]
                TransactionType.BUY_GOODS -> bodyLowerCase.split("to ")[1].split(" on")[0]
                TransactionType.WITHDRAW -> bodyLowerCase.split("from ")[1].split(" new")[0]
                TransactionType.RECEIVE -> bodyLowerCase.split("from ")[1].split(" on")[0]
                TransactionType.AIRTIME -> null
                TransactionType.BALANCE -> null
                TransactionType.DEPOSIT -> bodyLowerCase.split("to ")[1].split(" new")[0]
                TransactionType.UNKNOWN -> null
            }
        }

    val transactionDate: Long
        get() {
            return when (type) {
                TransactionType.REVERSAL -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(" and")[0].replace("at", "")).timestamp
                TransactionType.SEND -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")).timestamp
                TransactionType.PAY_BILL -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(" new")[0].replace("at", "")).timestamp
                TransactionType.BUY_GOODS -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")).timestamp
                TransactionType.WITHDRAW -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split("on ")[1].split("withdraw")[0].replace("at", "")).timestamp
                TransactionType.RECEIVE -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")).timestamp
                TransactionType.AIRTIME -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")).timestamp
                TransactionType.BALANCE -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")).timestamp
                TransactionType.DEPOSIT -> DateTime.parse("d/M/yy  h:mm a", bodyLowerCase.split(" on ")[1].split(" give")[0].replace("at", "")).timestamp
                TransactionType.UNKNOWN -> 0
            }
        }

}