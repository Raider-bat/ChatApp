package com.example.chatapp

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.contact_item.view.*

class ContactItem(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.contact_name.text = user.userName
    }

    override fun getLayout(): Int {
        return R.layout.contact_item
    }
}