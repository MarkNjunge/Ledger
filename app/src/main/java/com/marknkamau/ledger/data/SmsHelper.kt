package com.marknkamau.ledger.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.marknkamau.ledger.data.models.MpesaMessage
import com.marknkamau.ledger.data.models.Sms
import com.marknkamau.ledger.data.models.TransactionType

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class SmsHelper(private val context: Context) {
    @SuppressLint("Recycle")
    fun getMpesaMessages(): MutableList<MpesaMessage> {
        val smsList = mutableListOf<Sms>()
        val returnList = mutableListOf<MpesaMessage>()

        val cursor = context.contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)
                ?: return returnList

        val bodyIndex = cursor.getColumnIndexOrThrow("body")
        val addressIndex = cursor.getColumnIndexOrThrow("address")
        val dateIndex = cursor.getColumnIndexOrThrow("date")

        if (bodyIndex < 0 || !cursor.moveToFirst()) returnList
        do {
            val address = cursor.getString(addressIndex)
            if (address == "MPESA") {
                val body = cursor.getString(bodyIndex)
                val date = cursor.getString(dateIndex)
                smsList.add(Sms(address, body, date.toLong()))
            }
        } while (cursor.moveToNext())

        cursor.close()

        return compile(smsList)
    }

    @SuppressLint("DefaultLocale")
    private fun compile(list: MutableList<Sms>): MutableList<MpesaMessage> {
        val messages = mutableListOf<MpesaMessage>()

        list.filter { it.body.isNotEmpty() }
                .forEach {
                    // Remove messages than are not transactions
                    if (it.body.toLowerCase().contains(Regex("(.{10} )(confirmed.)")))
                        messages.add(MpesaMessage(it.body, it.date))
                }

        return messages.filter { it.type != TransactionType.BALANCE }.toMutableList()
    }
}