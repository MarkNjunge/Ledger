package com.marknjunge.ledger.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.saveToClipboard(label: String, text: String) {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clipData)
}
