package com.marknjunge.ledger.ui.base

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.marknjunge.ledger.R

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(){
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}