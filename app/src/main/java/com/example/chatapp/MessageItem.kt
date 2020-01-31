package com.example.chatapp

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_view.view.*
import java.text.SimpleDateFormat

class MessageItem(val message: Message?): Item<GroupieViewHolder>() {
    constructor():this(null)

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_user.text = message!!.name
        viewHolder.itemView.message_time.text = SimpleDateFormat("HH:mm").format(message.time)
        viewHolder.itemView.message_text.text = message.text
    }

    override fun getLayout(): Int {
        return R.layout.list_item_view
    }

    override fun isClickable(): Boolean {
        return false
    }
    override fun isLongClickable(): Boolean {
        return false
    }
}