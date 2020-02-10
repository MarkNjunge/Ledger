package com.marknjunge.ledger.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    companion object {
        const val MESSAGE = "message"
    }

    private lateinit var mpesaMessage: MpesaMessage

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        mpesaMessage = intent.extras!!.get(MESSAGE) as MpesaMessage

        tvMessageCode.text = mpesaMessage.code
        tvMesageBody.text = mpesaMessage.body
        tvTransactionType.text = mpesaMessage.transactionType.name.capitalize()
        tvAmount.text = "Ksh. ${mpesaMessage.amount}"
        mpesaMessage.transactionType.positive?.let {
            val i = if (it) {
                ContextCompat.getColor(this@MessageActivity, R.color.positiveValue)
            } else {
                ContextCompat.getColor(this@MessageActivity, R.color.negativeValue)
            }
            tvTransactionType.setTextColor(i)
        }
    }
}
