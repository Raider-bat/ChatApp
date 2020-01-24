package com.example.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MessageAdapter.MessageViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
/*
* This is class don't work, but it can :)
*
 */
class MessageAdapter(options: FirebaseRecyclerOptions<Message?>) :
    FirebaseRecyclerAdapter<Message, MessageViewHolder>(options) {
    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        model: Message
    ) {
        holder.mes_text.text = model.text
        holder.mes_user.text = model.name
        holder.mes_time.text = model.time.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        return MessageViewHolder(view)
    }

    inner class MessageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mes_user: TextView
        var mes_time: TextView
        var mes_text: TextView

        init {
            mes_user = itemView.findViewById(R.id.message_user)
            mes_time = itemView.findViewById(R.id.message_time)
            mes_text = itemView.findViewById(R.id.message_text)
        }
    }
}