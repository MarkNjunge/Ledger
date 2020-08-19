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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.local.AppPreferences
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.transactionDetail.TransactionDetailActivity
import com.marknjunge.ledger.ui.transactions.TransactionsActivity
import com.marknjunge.ledger.utils.AppUpdate
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.openUrlInCustomTab
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModel()
    private val appPreferences: AppPreferences by inject()
    private val appUpdate: AppUpdate by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLoading()
        initializeRecyclerView()


        tvSeeMore.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        checkForUpdate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMessages()
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
            TransactionDetailActivity.start(this@MainActivity, message)
        }
        rvGroups.adapter = adapter

        viewModel.groupedMessages.observe(this, Observer { items ->
            if (items.isNotEmpty()) {
                tvBalance.text = CurrencyFormatter.format(getBalance(items, 0))
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

    private fun checkForUpdate() {
        appUpdate.getLatestVersion { version, url ->
            if (appUpdate.shouldUpdate(false)) {
                showAppUpdateDialog(version, url)
            }
        }
    }

    private fun showAppUpdateDialog(latestVersion: Int, url: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Update available")
            .setMessage("An update is available for Ledger!")
            .setPositiveButton("Download") { _, _ ->
                openUrlInCustomTab(url)
            }
            .setNegativeButton("Cancel") { _, _ -> }
            .setNeutralButton("Skip") { _, _ ->
                appPreferences.skipUpdateVer = latestVersion
            }
            .show()

    }

    private fun getBalance(messages: List<MpesaMessage>, index: Int): Double {
        return messages[index].balance ?: getBalance(messages, index + 1)
    }

}
