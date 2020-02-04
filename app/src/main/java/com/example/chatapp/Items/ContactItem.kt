package com.example.chatapp.Items


import com.example.chatapp.R
import com.example.chatapp.data.User
import com.example.chatapp.helpers.UserStatusHelper
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.contact_item.view.*


class ContactItem(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.contact_name.text = user.userName
        viewHolder.itemView.contact_state.text = UserStatusHelper()
            .userStatusPrinter(user.userStatus!!)
    }
    override fun getLayout(): Int {
        return R.layout.contact_item
    }
}