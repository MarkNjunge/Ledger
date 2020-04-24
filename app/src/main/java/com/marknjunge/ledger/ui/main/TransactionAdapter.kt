package com.marknjunge.ledger.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_transaction.view.*
import timber.log.Timber
import java.util.*

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class TransactionAdapter(private val context: Context, private val onClick: (MpesaMessage) -> Unit) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var data: List<MpesaMessage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(context, data[position], onClick)

    fun setItems(data: List<MpesaMessage>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(context: Context, mpesaMessage: MpesaMessage, onClick: (MpesaMessage) -> Unit) = with(itemView) {
            tvTransactionAccount.text = mpesaMessage.accountNumber ?: mpesaMessage.transactionType.string()

            val dateFormat = if (DateUtils.isToday(mpesaMessage.transactionDate)) "HH:mm a" else "EEEE, d MMMM"
            val time = DateTime.fromTimestamp(mpesaMessage.transactionDate).format(dateFormat)
            tvTransactionDate.text = time

            val transactionSign = when (mpesaMessage.transactionType.positive) {
                true -> "+"
                false -> "-"
                null -> ""
            }
            tvTransactionAmount.text = "$transactionSign ${CurrencyFormatter.format(mpesaMessage.amount)}"
            val color = when (mpesaMessage.transactionType.positive) {
                true -> ContextCompat.getColor(context, R.color.colorPositiveValue)
                false -> ContextCompat.getColor(context, R.color.colorNegativeValue)
                null -> ContextCompat.getColor(context, R.color.colorTextPrimary)
            }
            tvTransactionAmount.setTextColor(color)
            rootTransaction.setOnClickListener { onClick(mpesaMessage) }
        }
    }
}