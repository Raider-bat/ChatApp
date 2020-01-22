package com.example.chatapp

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.list_item_view.view.*

class MessageItem(var name: String?, var text: String, var time:String?, var uid:String): Item<GroupieViewHolder>() {
    constructor():this("","","","")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_user.text = name
        viewHolder.itemView.message_time.text = time
        viewHolder.itemView.message_text.text = text
    }

    override fun getLayout(): Int {
        return R.layout.list_item_view
    }
}