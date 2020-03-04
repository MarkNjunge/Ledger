package com.marknjunge.ledger.ui.transactions

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.transactionDetail.TransactionDetailActivity
import com.marknjunge.ledger.utils.*
import kotlinx.android.synthetic.main.activity_transactions.*
import org.koin.android.ext.android.inject
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class TransactionsActivity : BaseActivity() {

    private val transactionsViewModel: TransactionsViewModel by inject()
    private lateinit var transactionsAdapter: PagedTransactionsAdapter
    private val appPreferences: AppPreferences by inject()
    private val REQUEST_WRITE_FILE: Int = 43
    private var csvContent: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        supportActionBar?.title = "Transactions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeRecyclerView()
        getMessages()

        etSearch.onTextChanged {
            val term = etSearch.trimmedText
            if (term.isNotEmpty()) {
                search(term)
            } else {
                getMessages()
            }
        }

        showExportPrompt()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.menu_export)?.isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export -> {
                exportAsCsv()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_WRITE_FILE && data != null) {
                data.data?.let { uri ->
                    csvContent?.let {
                        val content = it.joinToString("\n").toByteArray()
                        SAFUtils.writeContent(contentResolver, uri, content)

                        Snackbar.make(getRootView(), "Transactions exported", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        rvTranasctions.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTranasctions.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        transactionsAdapter = PagedTransactionsAdapter(this) { message ->
            TransactionDetailActivity.start(this, message)
        }
        rvTranasctions.adapter = transactionsAdapter
    }

    private fun getMessages() {
        transactionsViewModel.getPagedMessages().observe(this, Observer { items ->
            transactionsAdapter.submitList(items)
            rvTranasctions.smoothScrollToPosition(0)
        })
    }

    private fun search(term: String) {
        transactionsViewModel.search(term).observe(this, Observer { items ->
            transactionsAdapter.submitList(items)
            rvTranasctions.smoothScrollToPosition(0)
        })
    }

    private fun showExportPrompt() {
        Handler().postDelayed({
            if (!appPreferences.hasSeenExportPrompt) {
                MaterialTapTargetPrompt.Builder(this)
                    .setTarget(R.id.menu_export)
                    .setPrimaryText("Export")
                    .setSecondaryText("You can export your transactions as csv")
                    .setIcon(R.drawable.ic_export)
                    .setBackgroundColour(ContextCompat.getColor(this, R.color.colorActionBarBackground))
                    .setPrimaryTextColour(Color.WHITE)
                    .setSecondaryTextColour(Color.WHITE)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setIconDrawableTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)))
                    .show()

                appPreferences.hasSeenExportPrompt = true
            }
        }, 1000)
    }

    private fun exportAsCsv() {
        transactionsViewModel.getMessagesForExport().observe(this, Observer { data ->
            csvContent = data
            val filename = "M-Pesa Transactions ${DateTime.now.format("yyyy-MM-dd HH:mm")}.csv"
            val intent = SAFUtils.getIntent("text/csv", filename)
            startActivityForResult(intent, REQUEST_WRITE_FILE)
        })
    }
}
