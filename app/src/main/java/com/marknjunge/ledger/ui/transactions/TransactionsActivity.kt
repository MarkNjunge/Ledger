package com.marknjunge.ledger.ui.transactions

import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.detail.TransactionActivity
import kotlinx.android.synthetic.main.activity_transactions.*
import org.koin.android.ext.android.inject

class TransactionsActivity : BaseActivity() {

    private val transactionsViewModel: TransactionsViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        supportActionBar?.title = "Transactions"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        rvTranasctions.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvTranasctions.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        val adapter = PagedTransactionsAdapter(this) { message ->
            TransactionActivity.start(this, message)
        }
        rvTranasctions.adapter = adapter

        transactionsViewModel.getPagedMessages().observe(this, Observer { items ->
            adapter.submitList(items)
        })

    }
}
