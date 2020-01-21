package com.example.chatapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_mes_item.view.*
import java.text.SimpleDateFormat

class LatestMessageItem(val message: Message) : Item<GroupieViewHolder>(){

    var partUser : User? = null
    override fun getLayout(): Int {
     return R.layout.latest_mes_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartId: String
        if (message.toUid == FirebaseAuth.getInstance().uid){
            chatPartId = message.uid
        }else{
            chatPartId = message.toUid
        }
        FirebaseDatabase.getInstance().reference.child("Users").child(chatPartId).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                partUser = p0.getValue(User::class.java)
                viewHolder.itemView.latest_mes_user_name.text = partUser?.userName
            }
        })

        if (message.text.length> 44){
            var len = message.text.length - 42
            viewHolder.itemView.latest_mes_text.text =  message.text.dropLast(len) +"..."
        }else{
            viewHolder.itemView.latest_mes_text.text =  message.text
        }
        val dateFormat = SimpleDateFormat("HH:mm")
        viewHolder.itemView.latest_mes_time.text = dateFormat.format(message.time)
    }
}