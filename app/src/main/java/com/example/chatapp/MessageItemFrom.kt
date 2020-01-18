package com.example.chatapp

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

import kotlinx.android.synthetic.main.list_item_view_from.view.*

class MessageItemFrom(var name: String?, var text: String, var time:String?, var uid:String): Item<GroupieViewHolder>() {
    constructor():this("","","","")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.message_time1.text = time
        viewHolder.itemView.message_text1.text = text
    }

    override fun getLayout(): Int {
        return R.layout.list_item_view_from

    }
}
