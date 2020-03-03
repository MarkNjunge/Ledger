package com.marknjunge.ledger.ui.transactions

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.detail.TransactionActivity
import com.marknjunge.ledger.utils.onTextChanged
import com.marknjunge.ledger.utils.trimmedText
import kotlinx.android.synthetic.main.activity_transactions.*
import org.koin.android.ext.android.inject
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class TransactionsActivity : BaseActivity() {

    private val transactionsViewModel: TransactionsViewModel by inject()
    private lateinit var transactionsAdapter: PagedTransactionsAdapter
    private val appPreferences: AppPreferences by inject()

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

    private fun initializeRecyclerView() {
        rvTranasctions.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTranasctions.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        transactionsAdapter = PagedTransactionsAdapter(this) { message ->
            TransactionActivity.start(this, message)
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
}
