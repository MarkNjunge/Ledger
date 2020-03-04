package com.marknjunge.ledger.utils

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.getRootView(): View = (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)