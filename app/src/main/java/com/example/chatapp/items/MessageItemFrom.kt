package com.example.chatapp.items

import com.example.chatapp.model.Message
import com.example.chatapp.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.list_item_view_from.view.*
import java.text.SimpleDateFormat

class MessageItemFrom(val message: Message?): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_time1.text = SimpleDateFormat("HH:mm").format(message!!.time)
        viewHolder.itemView.message_text1.text = message.text
    }

    override fun getLayout(): Int {
        return R.layout.list_item_view_from
    }
}