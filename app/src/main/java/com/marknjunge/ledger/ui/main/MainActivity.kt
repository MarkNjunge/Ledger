package com.marknjunge.ledger.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.marknjunge.ledger.R
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.ui.detail.MessageActivity
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.DateTime
import com.marknjunge.ledger.utils.SAFUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : BaseActivity() {

    private val REQUEST_READ_SMS: Int = 1
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLoading()
        initializeRecyclerView()

        readSms()
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
            val i = Intent(this@MainActivity, MessageActivity::class.java)
            i.putExtra(MessageActivity.MESSAGE, message)
            startActivity(i)
        }
        rvGroups.adapter = adapter

        viewModel.groupedMessages.observe(this, Observer { items ->
            tvBalance.text = CurrencyFormatter.format(items.first().balance)
            adapter.setItems(items.take(4))
            TransitionManager.beginDelayedTransition(rootMainActivity)
            contentMainActivity.visibility = View.VISIBLE
        })
    }

    private fun readSms() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_SMS),
                REQUEST_READ_SMS
            )
        } else {
            viewModel.getMessages()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_SMS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                readSms()
            }
        }
    }

}
