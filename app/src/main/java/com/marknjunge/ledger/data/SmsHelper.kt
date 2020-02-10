package com.marknjunge.ledger.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Telephony
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.data.models.Sms

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class SmsHelper(private val context: Context) {

    @SuppressLint("Recycle")
    private fun getRawMessages(): List<Sms> {
        val messageList = mutableListOf<Sms>()

        val messagesCursor =
            context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                null,
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER
            ) ?: throw RuntimeException("Unable to get groupedMessages")

        val bodyIndex = messagesCursor.getColumnIndexOrThrow("body")
        val addressIndex = messagesCursor.getColumnIndexOrThrow("address")
        val dateIndex = messagesCursor.getColumnIndexOrThrow("date")

        messagesCursor.moveToFirst()
        do {
            val address = messagesCursor.getString(addressIndex)
            val date = messagesCursor.getString(dateIndex)

            if (address == "MPESA") {
                val body = messagesCursor.getString(bodyIndex)
                messageList.add(Sms(address, body, date.toLong()))
            }
        } while (messagesCursor.moveToNext())

        messagesCursor.close()

        return messageList
    }

    @SuppressLint("Recycle")
    fun getMpesaMessages(): List<MpesaMessage> {
        return compile(getRawMessages())
    }

    @SuppressLint("DefaultLocale")
    private fun compile(list: List<Sms>): List<MpesaMessage> {
        return list.filter { it.body.isNotEmpty() }
            // Remove groupedMessages than are not transactions
            .filter { it.body.toLowerCase().contains(Regex("(.{10} )(confirmed.)")) }
            .map { MpesaMessage.create(it.body) }
    }
}