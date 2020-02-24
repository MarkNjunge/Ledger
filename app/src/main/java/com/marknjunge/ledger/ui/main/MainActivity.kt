package com.marknjunge.ledger.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.marknjunge.ledger.R
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.detail.TransactionActivity
import com.marknjunge.ledger.ui.transactions.TransactionsActivity
import com.marknjunge.ledger.utils.CurrencyFormatter
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLoading()
        initializeRecyclerView()

        viewModel.getMessages()

        tvSeeMore.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }
    }

    private fun initializeLoading() {
        viewModel.loading.observe(this, Observer { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        })
    }

    private fun initializeRecyclerView() {
        rvGroups.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvGroups.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayout.VERTICAL))

        val adapter = TransactionAdapter(this@MainActivity) { message ->
            TransactionActivity.start(this@MainActivity, message)
        }
        rvGroups.adapter = adapter

        viewModel.groupedMessages.observe(this, Observer { items ->
            if (items.isNotEmpty()) {
                tvBalance.text = CurrencyFormatter.format(items.first().balance)
                adapter.setItems(items.take(4))
                TransitionManager.beginDelayedTransition(rootMainActivity)
                contentMainActivity.visibility = View.VISIBLE
                contentNoMessages.visibility = View.GONE
            } else {
                contentNoMessages.visibility = View.VISIBLE
                contentMainActivity.visibility = View.GONE
            }
        })
    }

}
