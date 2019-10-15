package com.marknjunge.ledger.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_SMS: Int = 1
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLoading()
        initializeRecyclerView()

        readSms()

        btnExport.setOnClickListener {
            viewModel.exportAsCSV()
            Toast.makeText(this, "Exporting to ledger/transactions.csv", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()

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

        val adapter = GroupMessageAdapter(this@MainActivity) { message ->
            val i = Intent(this@MainActivity, MessageActivity::class.java)
            i.putExtra(MessageActivity.MESSAGE, message)
            startActivity(i)
        }
        rvGroups.adapter = adapter

        viewModel.messages.observe(this, Observer { items ->
            adapter.setItems(items)
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
