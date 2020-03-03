package com.marknjunge.ledger.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.marknjunge.ledger.R

fun Context.openUrlInCustomTab(url: String) {
    val intent = CustomTabsIntent.Builder()
        .setToolbarColor(ContextCompat.getColor(this, R.color.colorActionBarBackground))
        .setShowTitle(true)
        .build()
    intent.launchUrl(this, Uri.parse(url))
}