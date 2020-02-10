package com.marknjunge.ledger.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_message.view.*
import java.util.*

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class MessageAdapter(private val context: Context, private val onClick: (MpesaMessage) -> Unit) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private var data: List<MpesaMessage> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
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
            val time = DateTime.fromTimestamp(mpesaMessage.transactionDate).format("HH:mm a")
            tvTime.text = time
            tvAmount.text = "Ksh. ${mpesaMessage.amount}"
            tvType.text = mpesaMessage.transactionType.toString()
            mpesaMessage.transactionType.positive?.let {
                val i = if (it) {
                    ContextCompat.getColor(context, R.color.positiveValue)
                } else {
                    ContextCompat.getColor(context, R.color.negativeValue)
                }
                tvType.setTextColor(i)
            }
            viewMessage.setOnClickListener { onClick(mpesaMessage) }
        }
    }
}