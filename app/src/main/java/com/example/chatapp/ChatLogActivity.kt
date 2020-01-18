package com.example.chatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatLogActivity : BaseSwipeToDismissActivity() {

    lateinit var recyclerView: RecyclerView
    var adapter = GroupAdapter<GroupieViewHolder>()
     lateinit var user : User
    val channel_id ="mes_id"
    val channel_name = "mes_name"
    val Channel_desc = "mes_notif"

    override fun isActivity(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
         user = intent.getParcelableExtra<User>(ContactsActivity.USER_KEY)


        supportActionBar?.title = user.userName

        recyclerView = findViewById(R.id.chat_log_list_message)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.setStackFromEnd(true)
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.setLayoutManager(layoutManager)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channel_id,
                channel_name, NotificationManager.IMPORTANCE_HIGH
            )
            mChannel.description = Channel_desc
            var notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(mChannel)
            notificationManager.cancel(2)
        }


           recyclerView.adapter = adapter
        writeMessage()
        lisMessage()
    }

    private fun lisMessage(){
        val myUid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("Chats").child(myUid!!).child(user.uid!!).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

                val mes = p0.getValue(MessageItem::class.java)
                if (mes != null){
                    if (mes.uid == FirebaseAuth.getInstance().uid){
                        adapter.add(MessageItemFrom(mes.name,mes.text,mes.time,mes.uid))

                    }else {
                        adapter.add(MessageItem(mes.name, mes.text, mes.time, mes.uid))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                return
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                return
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                return
            }

            override fun onChildRemoved(p0: DataSnapshot) {

                adapter.removeGroupAtAdapterPosition(recyclerView.adapter!!.itemCount-1)

                return
            }
        })
    }

    private fun writeMessage(){

        val myUid = FirebaseAuth.getInstance().uid
        chat_log_send_Button.setOnClickListener(View.OnClickListener {
            var user_nameInDB = FirebaseAuth.getInstance().currentUser?.displayName

            if (user_nameInDB == null){
                user_nameInDB = FirebaseAuth.getInstance().currentUser?.phoneNumber
            }

            val lEditText = chat_log_edit_message.text.toString().trim()
            if (lEditText.length != 0 ) {
                //  recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

                val currentDateTime = LocalDateTime.now()
                val timeNow = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                val messageData = Message(user_nameInDB, lEditText, timeNow, myUid!!, user.uid!!)
                FirebaseDatabase.getInstance().reference.child("Chats").child(myUid!!).child(user.uid!!).push().setValue(messageData)
                if (myUid != user.uid){
                FirebaseDatabase.getInstance().reference.child("Chats").child(user.uid!!).child(myUid!!).push().setValue(messageData)
                }
                chat_log_edit_message.setText("")

                FirebaseDatabase.getInstance().reference.child("LatestMessage").child(myUid!!).child(user.uid!!).setValue(messageData)
                FirebaseDatabase.getInstance().reference.child("LatestMessage").child(user.uid!!).child(myUid!!).setValue(messageData)
            }
        })


    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}
