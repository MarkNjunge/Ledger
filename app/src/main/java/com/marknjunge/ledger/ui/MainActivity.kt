package com.marknjunge.ledger.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.utils.SAFUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_SMS: Int = 1
    private val REQUEST_WRITE_FILE: Int = 43
    private val viewModel: MainViewModel by viewModel()
    private var csvContent: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeLoading()
        initializeRecyclerView()

        readSms()

        btnExport.setOnClickListener {
            viewModel.getMessagesForCsv().observe(this, Observer { csvContent ->
                this.csvContent = csvContent
                val intent = SAFUtils.getIntent("text/csv", "M-Pesa Transactions.csv")
                startActivityForResult(intent, REQUEST_WRITE_FILE)
            })
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

        viewModel.groupedMessages.observe(this, Observer { items ->
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_WRITE_FILE) {
            Timber.d(data?.data.toString())

            csvContent?.let {
                val content = it.joinToString("\n").toByteArray()
                SAFUtils.writeContent(contentResolver, data!!.data!!, content)
            }
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
