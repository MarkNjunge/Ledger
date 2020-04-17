package com.marknjunge.ledger.ui.transactions

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.CurrencyFormatter
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_transaction.view.*

class PagedTransactionsAdapter(
    private val context: Context,
    private val onClick: (MpesaMessage) -> Unit
) : PagedListAdapter<MpesaMessage, PagedTransactionsAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(context, it, onClick)
        }
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
            mpesaMessage.transactionType.positive?.let {
                val color = if (it) {
                    ContextCompat.getColor(context, R.color.colorPositiveValue)
                } else {
                    ContextCompat.getColor(context, R.color.colorNegativeValue)
                }
                tvTransactionAmount.setTextColor(color)
            }
            rootTransaction.setOnClickListener { onClick(mpesaMessage) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MpesaMessage>() {
            // The ID property identifies when items are the same.
            override fun areItemsTheSame(oldItem: MpesaMessage, newItem: MpesaMessage) =
                oldItem.code == newItem.code

            // If you use the "==" operator, make sure that the object implements
            // .equals(). Alternatively, write custom data comparison logic here.
            override fun areContentsTheSame(
                oldItem: MpesaMessage, newItem: MpesaMessage
            ) = oldItem.code == newItem.code
        }
    }
}