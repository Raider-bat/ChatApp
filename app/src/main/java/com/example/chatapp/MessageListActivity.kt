package com.example.chatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_message_list.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageListActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MessageAdapter

    var adapterg = GroupAdapter<GroupieViewHolder>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)



        FirebaseMessaging.getInstance().subscribeToTopic("Message")
        FirebaseMessaging.getInstance().subscribeToTopic("MyMessage")
        recyclerView = findViewById(R.id.list_message)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)


      /*  recyclerView.addOnLayoutChangeListener(View.OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom  ->
            if (bottom< oldBottom){

                recyclerView.post {
                    recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount-1)


                }
            }


        }) */


        layoutManager.setStackFromEnd(true)
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.setLayoutManager(layoutManager)


            // Вытаскиваем сообщения из БД
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("Message")
            ) { snapshot ->

                val mes = snapshot.getValue(Message::class.java)!!

                   // mes.time = timeNow
                mes
            }
            .build()


        recyclerView.adapter = adapterg


        adapter = MessageAdapter(options)
        adapter.startListening()

        //Запись сообщения в базу данных
        writeMessage()
        //Слушатель изменения в базе данных
        lisMessage()




       // FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(50000000)
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }


   private fun lisMessage(){
        FirebaseDatabase.getInstance().reference.child("Message").addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                   recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

                val mes = p0.getValue(MessageItem::class.java)
                if (mes != null){
                    if (mes.uid == FirebaseAuth.getInstance().uid){
                        adapterg.add(MessageItemFrom(mes.name,mes.text,mes.time,mes.uid))

                    }else {
                        adapterg.add(MessageItem(mes.name, mes.text, mes.time, mes.uid))
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

                    adapterg.removeGroupAtAdapterPosition(recyclerView.adapter!!.itemCount-1)

                return
            }
        })
   }


    //Запись сообщения в базу данных
    private fun writeMessage() {

        val uid = FirebaseAuth.getInstance().uid
        send_Button.setOnClickListener(View.OnClickListener {
            var user_nameInDB = FirebaseAuth.getInstance().currentUser?.displayName

            if (user_nameInDB == null){
                user_nameInDB = FirebaseAuth.getInstance().currentUser?.phoneNumber
            }

            val lEditText = edit_message.text.toString().trim()
            if (lEditText.length != 0 ) {
              //  recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

                val currentDateTime = LocalDateTime.now()
                val timeNow = currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                FirebaseDatabase.getInstance().getReference("Message").push().setValue(
                    Message(
                        user_nameInDB, lEditText, timeNow,uid!!,""
                    )
                )
                edit_message.setText("")
            }
        })
    }



    override fun onStart() {
        super.onStart()
        //adapter.startListening()
        if(FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
       // adapter.stopListening()

    }
}

