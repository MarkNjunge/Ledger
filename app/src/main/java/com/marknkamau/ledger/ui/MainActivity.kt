package com.marknkamau.ledger.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.marknkamau.ledger.R
import com.marknkamau.ledger.data.SmsHelper
import com.marknkamau.ledger.data.models.MessageGroup
import com.marknkamau.ledger.data.models.MpesaMessage
import com.marknkamau.ledger.utils.DateTime
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val REQUEST_READ_SMS: Int = 1
        private lateinit var adapter: GroupMessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvGroups.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvGroups.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayout.VERTICAL))

        adapter = GroupMessageAdapter(this@MainActivity) { message ->
            val i = Intent(this@MainActivity, MessageActivity::class.java)
            i.putExtra(MessageActivity.MESSSAGE, message)
            startActivity(i)
        }
        rvGroups.adapter = adapter

        readSms()
    }

    private fun readSms() {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_SMS), REQUEST_READ_SMS)
        } else {
            val smsHelper = SmsHelper()
            val sms = smsHelper.getMpesaMessages(this@MainActivity)

            val byDate = groupByDate(sms)

            adapter.setItems(byDate)
        }
    }

    private fun groupByDate(messages: List<MpesaMessage>): MutableList<MessageGroup> {
        val treeMap = HashMap<Long, MutableList<MpesaMessage>>()

        messages.forEach {
            // Reduce the date's accuracy to only up to the day of the month
            val dateTime = DateTime.fromTimestamp(it.date)
            val bigDateTime = DateTime(dateTime.year, dateTime.month, dateTime.dayOfMonth, 0, 0, 0, 0)
            val date = bigDateTime.timestamp

            // If the a messages does not exist for the date, create it
            if (treeMap[date] == null) {
                treeMap[date] = mutableListOf()
            }

            // Add the message to the messages
            treeMap[date]!!.add(it)
        }

        // Create a list of messages from the map
        val list = mutableListOf<MessageGroup>()
        for (key in treeMap.keys) {
            list.add(MessageGroup(key, treeMap[key]!!))
        }

        return list.sortedBy { it.date }.reversed().toMutableList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_SMS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                readSms()
            }
        }
    }

}
