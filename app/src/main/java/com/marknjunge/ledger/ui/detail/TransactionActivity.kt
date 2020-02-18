package com.marknjunge.ledger.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import androidx.core.content.ContextCompat
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.ui.base.BaseActivity
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.synthetic.main.activity_transaction.*

class TransactionActivity : BaseActivity() {

    private lateinit var mpesaMessage: MpesaMessage

    companion object {

        private const val MESSAGE = "message"

        fun start(context: Context, message: MpesaMessage) {
            val i = Intent(context, TransactionActivity::class.java)
            i.putExtra(MESSAGE, message)
            context.startActivity(i)
        }

    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        mpesaMessage = intent.extras!!.get(MESSAGE) as MpesaMessage

        supportActionBar?.title = mpesaMessage.code

        tvMessageCode.text = mpesaMessage.code
        tvMessageBody.text = mpesaMessage.body
        tvTransactionType.text = mpesaMessage.transactionType.string()

        val time = DateTime.fromTimestamp(mpesaMessage.transactionDate).format("HH:mm a EEEE, d MMMM")
        tvTransactionDate.text = time

        val transactionSign = if (mpesaMessage.transactionType.positive == true) "+" else "-"
        tvTransactionAmount.text = "$transactionSign ${CurrencyFormatter.format(mpesaMessage.amount)}"
        mpesaMessage.transactionType.positive?.let {
            val i = if (it) {
                ContextCompat.getColor(this@TransactionActivity, R.color.colorPositiveValue)
            } else {
                ContextCompat.getColor(this@TransactionActivity, R.color.colorNegativeValue)
            }
            tvTransactionAmount.setTextColor(i)
        }

        tvTransactionCost.text = CurrencyFormatter.format(mpesaMessage.transactionCost)
    }
}
