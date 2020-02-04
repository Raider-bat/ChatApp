package com.example.chatapp.Items

import com.example.chatapp.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.message_date_item.view.*

class MessageDateItem(private val time: String) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.message_date_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.date_message.text = time
    }

    override fun isClickable(): Boolean {
        return false
    }

    override fun isLongClickable(): Boolean {
        return false
    }
}