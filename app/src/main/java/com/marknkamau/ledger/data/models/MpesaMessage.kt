package com.marknkamau.ledger.data.models

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */
data class MpesaMessage(val body: String, val date: Long) : Parcelable {
    val code = body.split(Regex("( [Cc]onfirmed)"))[0].reversed().substring(0, 10).reversed()

    val type: TransactionType
        get() {
            return when {
                body.toLowerCase().contains(Regex("(.*) reversal (.*)")) -> TransactionType.REVERSAL
                body.toLowerCase().contains(Regex("(.*) sent to (.*)")) -> TransactionType.SEND
                body.toLowerCase().contains(Regex("(.*) paid to (.*)")) -> TransactionType.BUY
                body.toLowerCase().contains(Regex("(.*)withdraw (.*)")) -> TransactionType.WITHDRAW
                body.toLowerCase().contains(Regex("(.*) received (.*)")) -> TransactionType.RECEIVE
                body.toLowerCase().contains(Regex("(.*) airtime (.*)")) -> TransactionType.AIRTIME
                body.toLowerCase().contains(Regex("(.*)your m-pesa balance (.*)")) -> TransactionType.BALANCE
                body.toLowerCase().contains(Regex("(.*) give ksh(.*)")) -> TransactionType.DEPOSIT
                else -> TransactionType.UNKNOWN
            }
        }

    val amount: Double
        get() {
            return body.split("Ksh")[1].split(" ")[0].replace(",", "").toDouble()
        }

    // TODO Use parcelize
    constructor(source: Parcel) : this(
            source.readString()!!,
            source.readLong()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(body)
        writeLong(date)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MpesaMessage> = object : Parcelable.Creator<MpesaMessage> {
            override fun createFromParcel(source: Parcel): MpesaMessage = MpesaMessage(source)
            override fun newArray(size: Int): Array<MpesaMessage?> = arrayOfNulls(size)
        }
    }
}