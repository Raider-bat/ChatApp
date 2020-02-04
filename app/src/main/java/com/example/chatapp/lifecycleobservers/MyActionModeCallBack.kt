package com.example.chatapp.lifecycleobservers

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.example.chatapp.Items.MessageItemFrom
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class MyActionModeCallBack(var itemm: Item<GroupieViewHolder>?,
                           var adapter: GroupAdapter<GroupieViewHolder> ) : ActionMode.Callback {
    companion object{
        var mActionMode: ActionMode? = null
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.delete_message ->{

                val messageItem = itemm as MessageItemFrom
                val mes = messageItem.message
                val myUid = FirebaseAuth.getInstance().uid
                FirebaseDatabase.getInstance().reference.child("Chats").child(myUid!!)
                    .child(mes!!.toUid)
                    .orderByChild("time")
                    .equalTo(mes.time.toDouble())
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            p0.children.forEach {
                                it.ref.removeValue()
                            }
                        }
                    })
                if (myUid != mes.toUid) {
                    FirebaseDatabase.getInstance().reference.child("Chats")
                        .child(mes!!.toUid)
                        .child(myUid!!).orderByChild("time")
                        .equalTo(mes.time.toDouble())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                p0.children.forEach {
                                    it.ref.removeValue()
                                }
                            }
                        })
                }
                adapter.removeGroupAtAdapterPosition(adapter
                        .getAdapterPosition(itemm as MessageItemFrom))
                mode!!.finish()
                return true
            }
            else -> return false
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode!!.menuInflater.inflate(R.menu.context_menu,menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        mActionMode = null
    }
}