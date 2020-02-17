package com.marknjunge.ledger.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.repository.MessagesRepository
import com.marknjunge.ledger.utils.CsvUtils
import com.marknjunge.ledger.utils.DateTime
import com.marknjunge.ledger.utils.SAFUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private val messagesRepository: MessagesRepository by inject()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val REQUEST_WRITE_FILE: Int = 43
    private var csvContent: List<String>? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export -> {
                exportAsCsv()
                true
            }
            R.id.menu_settings -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_WRITE_FILE && data != null) {
                Timber.d(data.data.toString())

                data.data?.let { uri ->
                    csvContent?.let {
                        val content = it.joinToString("\n").toByteArray()
                        SAFUtils.writeContent(contentResolver, uri, content)

                        Toast.makeText(this, "Transactions exported", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun exportAsCsv() {
        coroutineScope.launch {
            val messages = messagesRepository.getMessages()
            this@BaseActivity.csvContent = CsvUtils.generateCsv(messages)

            val intent =
                SAFUtils.getIntent("text/csv", "M-Pesa Transactions ${DateTime.now.format("yyyy-MM-dd HH:mm")}.csv")
            startActivityForResult(intent, REQUEST_WRITE_FILE)
        }
    }
}