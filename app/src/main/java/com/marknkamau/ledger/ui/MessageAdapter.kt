package com.marknkamau.ledger.ui

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marknkamau.ledger.R
import com.marknkamau.ledger.data.models.MpesaMessage
import com.marknkamau.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_message.view.*
import timber.log.Timber
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
//            Timber.d(mpesaMessage.body)
            val time = DateTime.fromTimestamp(mpesaMessage.date).format("HH:mm a")
            tvTime.text = time
            tvAmount.text = "Ksh. ${mpesaMessage.amount}"
            tvType.text = mpesaMessage.type.toString()
            mpesaMessage.type.positive?.let {
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