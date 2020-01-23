package com.example.chatapp

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.contact_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ContactItem(val user: User): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.contact_name.text = user.userName
        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid!!).child("UserStatus").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userStatus = p0.getValue(UserStatus::class.java)?:return
                val dateNowMessage ="был(а) в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
                val timeDifference = (SimpleDateFormat("d").format(Date().time).toInt())- (SimpleDateFormat("d").format(userStatus.time).toInt())
                val statusNow = when {
                    userStatus.state == "в сети" -> {
                        userStatus.state
                    }
                    timeDifference == 1 -> {
                        "был(а) вчера в " + SimpleDateFormat("HH:mm").format(userStatus.time)
                    }
                    timeDifference in 2..7 -> {
                        "был(а) на этой неделе"
                    }
                    timeDifference > 7 -> {
                        "был(а) давно"
                    }
                    else -> {
                        dateNowMessage
                    }
                }
                viewHolder.itemView.contact_state.text = statusNow
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.contact_item
    }
}