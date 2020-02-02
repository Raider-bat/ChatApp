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
import java.util.*

class LatestMessageItem(val message: Message) : Item<GroupieViewHolder>(){

    var partUser : User? = null
    override fun getLayout(): Int {
     return R.layout.latest_mes_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartId = if (message.toUid == FirebaseAuth.getInstance().uid){
            message.uid
        }else{
            message.toUid
        }
        FirebaseDatabase.getInstance().reference.child("Users").child(chatPartId).addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                partUser = p0.getValue(User::class.java)

            }
        })
        FirebaseDatabase.getInstance().reference.child("Users").child(chatPartId).child("userName").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null){
                    viewHolder.itemView.latest_mes_user_name.text = "DELETED"
                }else{
                    viewHolder.itemView.latest_mes_user_name.text = p0.value.toString()
                }
            }
        })

        if (message.text.length> 44){
            val len = message.text.length - 42
            viewHolder.itemView.latest_mes_text.text =  message.text.dropLast(len) +"..."
        }else{
            viewHolder.itemView.latest_mes_text.text =  message.text
        }
        val dateFormatHM = SimpleDateFormat("HH:mm")
        val dateFormatDay = SimpleDateFormat("E")
        val dateFormatDM = SimpleDateFormat("d MMM")
        when(SimpleDateFormat("d").format(Date().time).toInt() -  SimpleDateFormat("d").format(message.time).toInt()){
            0 -> viewHolder.itemView.latest_mes_time.text = dateFormatHM.format(message.time)

            in 1..7 -> viewHolder.itemView.latest_mes_time.text = dateFormatDay.format(message.time)

            else ->viewHolder.itemView.latest_mes_time.text = dateFormatDM.format(message.time)
        }
    }
}