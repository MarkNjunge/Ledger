package com.marknjunge.ledger.ui.transactionDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.DateTime
import com.marknjunge.ledger.utils.saveToClipboard
import kotlinx.android.synthetic.main.activity_transaction_detail.*

class TransactionDetailActivity : BaseActivity() {

    private lateinit var mpesaMessage: MpesaMessage

    companion object {

        private const val MESSAGE = "message"

        fun start(context: Context, message: MpesaMessage) {
            val i = Intent(context, TransactionDetailActivity::class.java)
            i.putExtra(MESSAGE, message)
            context.startActivity(i)
        }

    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_detail)

        mpesaMessage = intent.extras!!.get(MESSAGE) as MpesaMessage

        supportActionBar?.title = mpesaMessage.code

        setUiData()
        setOnClickListeners()
    }

    private fun setUiData() {
        tvTransactionCode.text = mpesaMessage.code
        tvMessageBody.text = mpesaMessage.body
        tvTransactionType.text = mpesaMessage.transactionType.string()

        val time = DateTime.fromTimestamp(mpesaMessage.transactionDate).format("HH:mm a EEEE, d MMMM")
        tvTransactionDate.text = time

        val transactionSign = if (mpesaMessage.transactionType.positive == true) "+" else "-"
        tvTransactionAmount.text = "$transactionSign ${CurrencyFormatter.format(mpesaMessage.amount)}"
        mpesaMessage.transactionType.positive?.let {
            val i = if (it) {
                ContextCompat.getColor(this@TransactionDetailActivity, R.color.colorPositiveValue)
            } else {
                ContextCompat.getColor(this@TransactionDetailActivity, R.color.colorNegativeValue)
            }
            tvTransactionAmount.setTextColor(i)
        }

        tvTransactionCost.text = CurrencyFormatter.format(mpesaMessage.transactionCost)
    }

    private fun setOnClickListeners() {
        tvTransactionCode.setOnLongClickListener {
            saveToClipboard("code", mpesaMessage.code)
            Toast.makeText(this, "M-Pesa code saved to clipboard", Toast.LENGTH_SHORT).show()
            true
        }

        tvMessageBody.setOnLongClickListener {
            saveToClipboard("message", mpesaMessage.body)
            Toast.makeText(this, "M-Pesa message saved to clipboard", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        menu?.findItem(R.id.menu_share)?.isVisible = true

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share -> {
                shareMessage()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareMessage() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, mpesaMessage.body)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, "Share via..."))
    }
}
