package com.marknjunge.ledger.ui.base

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.ui.settings.SettingsActivity
import com.marknjunge.ledger.ui.transactions.TransactionsActivity
import com.marknjunge.ledger.utils.AppUpdate
import com.marknjunge.ledger.utils.openUrlInCustomTab
import org.koin.android.ext.android.inject

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val appPreferences: AppPreferences by inject()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.menu_update)?.isVisible = AppUpdate.shouldUpdate(appPreferences, true)

        if (this !is TransactionsActivity) {
            menu?.findItem(R.id.menu_export)?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_update -> {
                openUrlInCustomTab("https://github.com/MarkNjunge/Ledger/releases")
                true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}