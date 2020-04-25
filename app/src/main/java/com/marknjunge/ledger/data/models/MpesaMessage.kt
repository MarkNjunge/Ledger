package com.marknjunge.ledger.data.models

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.paging.DataSource
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.marknjunge.ledger.data.models.exception.MessageParseException
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.parcel.Parcelize
import timber.log.Timber
import java.lang.Exception

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */
@Parcelize
data class MpesaMessage(
        val body: String,
        val code: String,
        val transactionType: TransactionType,
        val amount: Double,
        val accountNumber: String?,
        val transactionDate: Long,
        val balance: Double,
        val transactionCost: Double
) : Parcelable {

    companion object {
        @SuppressLint("DefaultLocale")
        fun create(body: String, date: Long? = null): MpesaMessage {
            try {
                val code = body
                    .replace("Congratulations! ", "")
                    .replace("\n", " ")
                    .split(Regex("( [Cc]onfirmed)"))[0]

                val bodyLowerCase = body.toLowerCase()

                val transactionType = when {
                    bodyLowerCase.contains(Regex("(.*) reversal (.*)")) -> TransactionType.REVERSAL
                    bodyLowerCase.contains(Regex("(.*) sent to (.*) for account (.*)")) -> TransactionType.PAY_BILL
                    bodyLowerCase.contains(Regex("(.*) paid to")) -> TransactionType.BUY_GOODS
                    bodyLowerCase.contains(Regex("(.*) sent to (.*)")) -> TransactionType.SEND
                    bodyLowerCase.contains(Regex("(.*)withdraw (.*)")) -> TransactionType.WITHDRAW
                    bodyLowerCase.contains(Regex("(.*) received ksh(.*)")) -> TransactionType.RECEIVE
                    bodyLowerCase.contains(Regex("(.*) airtime on(.*)")) -> TransactionType.AIRTIME
                    bodyLowerCase.contains(Regex("(.*) received airtime (.*)")) -> TransactionType.AIRTIME_RECEIVE
                    bodyLowerCase.contains(Regex("(.*)your m-pesa balance (.*)")) -> TransactionType.BALANCE
                    bodyLowerCase.contains(Regex("(.*) give (.*)")) -> TransactionType.DEPOSIT
                    bodyLowerCase.contains(Regex("has been used to (.*) pay your (.*) fuliza")) -> TransactionType.FULIZA_PAY
                    else -> TransactionType.UNKNOWN
                }

                val amount = body.split("Ksh")[1].trim().split(" ")[0].replace(",", "").toDouble()

                val accountNumber = when (transactionType) {
                    TransactionType.REVERSAL -> null
                    TransactionType.SEND -> body.split("to ")[1].split(" on")[0]
                    TransactionType.PAY_BILL -> body.split("to ")[1].split(" on")[0]
                    TransactionType.BUY_GOODS -> body.split("to ")[1].split(" on")[0]
                    TransactionType.WITHDRAW -> body.split("from ")[1].split(" New")[0]
                    TransactionType.RECEIVE -> body.split("from ")[1].split(" on")[0]
                    TransactionType.AIRTIME -> null
                    TransactionType.AIRTIME_RECEIVE -> body.split("from ")[1].split(" on")[0]
                    TransactionType.BALANCE -> null
                    TransactionType.DEPOSIT -> body.split("to ")[1].split(" New")[0]
                    TransactionType.FULIZA_PAY -> null
                    TransactionType.UNKNOWN -> null
                }

                val transationDate = when (transactionType) {
                    TransactionType.REVERSAL -> {
                        val source = bodyLowerCase.split(" on ")[1].split(" and")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.SEND -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.PAY_BILL -> {
                        val source = bodyLowerCase.split(" on ")[1].split(" new")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.BUY_GOODS -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.WITHDRAW -> {
                        val source = bodyLowerCase.split("on ")[1].split("withdraw")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.RECEIVE -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.AIRTIME -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.AIRTIME_RECEIVE -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.BALANCE -> {
                        val source = bodyLowerCase.split(" on ")[1].split(".")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.DEPOSIT -> {
                        val source = bodyLowerCase.split(" on ")[1].split(" give")[0].replace("at", "")
                        DateTime.parse("d/M/yy  h:mm a", source).timestamp
                    }
                    TransactionType.FULIZA_PAY -> date!!
                    TransactionType.UNKNOWN -> 0
                }

                val balance = when (transactionType) {
                    TransactionType.REVERSAL -> {
                        body.split("balance is Ksh")[1]
                                .dropLast(1)
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.SEND -> {
                        body.split("balance is Ksh")[1]
                                .split(". Transaction cost")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.PAY_BILL -> {
                        body.split("balance is Ksh")[1]
                                .split(". Transaction cost")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.BUY_GOODS -> {
                        body.split("balance is Ksh")[1]
                                .split(". Transaction cost")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.WITHDRAW -> {
                        body.split("balance is Ksh")[1]
                                .split(". Transaction cost")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.RECEIVE -> {
                        body.split("balance is Ksh")[1]
                                .split(". ")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.AIRTIME -> {
                        body.split("balance is Ksh")[1]
                                .split(". Transaction cost")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.AIRTIME_RECEIVE -> 0.0
                    TransactionType.BALANCE -> {
                        body.split("balance was  Ksh")[1]
                                .split("  on")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.DEPOSIT -> {
                        body.split("balance is Ksh")[1]
                                .split(".")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.FULIZA_PAY -> {
                        body.split("balance is Ksh")[1]
                            .reversed()
                            .replaceFirst(".", "")
                            .reversed()
                            .replace(",", "")
                            .toDouble()
                    }
                    TransactionType.UNKNOWN -> 0.0
                }

                val transactionCost = when (transactionType) {
                    TransactionType.REVERSAL -> {
                        0.0
                    }
                    TransactionType.SEND -> {
                        body.split("Transaction cost, Ksh")[1]
                                .split(".")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.PAY_BILL -> {
                        body.split("Transaction cost, Ksh")[1]
                                .dropLast(1)
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.BUY_GOODS -> {
                        body.split("Transaction cost, Ksh")[1]
                                .dropLast(1)
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.WITHDRAW -> {
                        body.split("Transaction cost, Ksh")[1]
                                .dropLast(1)
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.RECEIVE -> 0.0
                    TransactionType.AIRTIME -> {
                        body.split("Transaction cost, Ksh")[1]
                                .split(".")[0]
                                .replace(",", "")
                                .toDouble()
                    }
                    TransactionType.AIRTIME_RECEIVE -> 0.0
                    TransactionType.BALANCE -> 0.0
                    TransactionType.DEPOSIT -> 0.0
                    TransactionType.FULIZA_PAY -> 0.0
                    TransactionType.UNKNOWN -> 0.0
                }

                return MpesaMessage(
                        body,
                        code,
                        transactionType,
                        amount,
                        accountNumber,
                        transationDate,
                        balance,
                        transactionCost
                )
            } catch (e: Exception) {
                Timber.e("Error parsing message: $body")
                throw MessageParseException(e.message ?: "Failed to parse message", body)
            }
        }

        fun fromEntity(entity: MpesaMessageEntity) = MpesaMessage(
                entity.body,
                entity.code,
                entity.transactionType,
                entity.amount,
                entity.accountNumber,
                entity.transactionDate,
                entity.balance,
                entity.transactionCost
        )
    }

    fun toEntity() = MpesaMessageEntity(
            0,
            code,
            transactionType,
            amount,
            accountNumber,
            transactionDate,
            balance,
            transactionCost,
            body
    )
}

fun List<MpesaMessageEntity>.toMessages() = this.map { MpesaMessage.fromEntity(it) }

fun DataSource.Factory<Int, MpesaMessageEntity>.toMessages() = this.map { MpesaMessage.fromEntity(it) }