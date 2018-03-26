package com.marknkamau.ledger.ui

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.marknkamau.ledger.R
import com.marknkamau.ledger.data.models.MessageGroup
import com.marknkamau.ledger.data.models.MpesaMessage
import com.marknkamau.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_group.view.*
import java.util.*

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class GroupMessageAdapter(private val context: Context,  private val onClick: (MpesaMessage) -> Unit) : RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>() {

    private var data: List<MessageGroup> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(context, data[position], onClick)

    fun setItems(data: List<MessageGroup>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: MessageGroup, onClick: (MpesaMessage) -> Unit) = with(itemView) {
            val date = DateTime.fromTimestamp(item.date).format("EE, dd - MMM - YY")
            tvHeaderText.text = date
            tvTransactions.text = if (item.messages.size > 1) "${item.messages.size} transactions" else "${item.messages.size} transaction"

            val messageAdapter = MessageAdapter(context, onClick)
            messageAdapter.setItems(item.messages)

            rvMessages.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rvMessages.adapter = messageAdapter
        }
    }
}