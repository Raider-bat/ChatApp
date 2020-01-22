package com.example.chatapp


import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.contact_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    var adapter = GroupAdapter<GroupieViewHolder>()
    lateinit var user : User
    val channel_id ="mes_id"
    val channel_name = "mes_name"
    val Channel_desc = "mes_notif"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
         user = intent.getParcelableExtra<User>(ContactsActivity.USER_KEY)

        supportActionBar?.title = user.userName
        userStatusAsActionBarSubTitle()
        recyclerView = findViewById(R.id.chat_log_list_message)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        notificationManager?.cancel(2)

        recyclerView.adapter = adapter

        emojiSetting()
        writeMessage()
        lisMessage()
    }

    private fun userStatusAsActionBarSubTitle() {
        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid!!).child("UserStatus").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userStatus = p0.getValue(UserStatus::class.java)?:return
                val dateNowMessage ="был(а) в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
                val timeDiffrence = (SimpleDateFormat("d").format(Date().time).toInt())- (SimpleDateFormat("d").format(userStatus.time).toInt())
                val statusNow = when {
                    userStatus.state =="в сети" -> {
                        userStatus.state
                    }
                    timeDiffrence ==1 -> {
                        "был(а) вчера в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
                    }
                    timeDiffrence in 2..7 -> {
                        "был(а) на этой неделе"
                    }
                    timeDiffrence >7 -> {
                        "был(а) давно"
                    }
                    else -> {
                        dateNowMessage
                    }
                }
                supportActionBar?.subtitle = statusNow
            }
        })
    }

    private fun emojiSetting() {
        val emojiButton = findViewById<ImageButton>(R.id.emoji_button)
        val emojiIcomAction = EmojIconActions(this,chat_log_activity, chat_log_edit_message,emojiButton,"#777777","#2C2C2C","#2C2C2C")
        emojiIcomAction.setIconsIds(R.drawable.ic_keyboard_grey_500_24dp,R.drawable.ic_insert_emoticon_grey_500_24dp)
        emojiIcomAction.ShowEmojIcon()
    }

    private fun lisMessage(){
        var dateLatestMessage : Int = 0
        val myUid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("Chats").child(myUid!!).child(user.uid!!).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

                val mes = p0.getValue(Message::class.java)
                if (mes != null){
                    val dateFormatForMessage = SimpleDateFormat("HH:mm")
                    val dateForDialog = SimpleDateFormat("d MMMM")
                    val dateNowMessage = SimpleDateFormat("d").format(mes.time).toInt()
                    if (dateNowMessage - dateLatestMessage !=0){
                        adapter.add(MessageDateItem(dateForDialog.format(mes.time)))
                    }

                    if (mes.uid == FirebaseAuth.getInstance().uid){
                        adapter.add(MessageItemFrom(mes.name,mes.text,dateFormatForMessage.format(mes.time),mes.uid))

                    }else {
                        adapter.add(MessageItem(mes.name, mes.text, dateFormatForMessage.format(mes.time), mes.uid))
                    }
                     dateLatestMessage = SimpleDateFormat("d").format(mes.time).toInt()
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

        val myUid = FirebaseAuth.getInstance().uid?:return
        chat_log_send_Button.setOnClickListener{
            var userNameInDB = FirebaseAuth.getInstance().currentUser?.displayName

            if (userNameInDB == null){
                userNameInDB = FirebaseAuth.getInstance().currentUser?.phoneNumber
            }

            val lEditText = chat_log_edit_message.text.toString().trim()
            if (lEditText.isNotEmpty()) {

                val dataTime = Date().time
                val messageData = Message(userNameInDB, lEditText, dataTime, myUid, user.uid!!)
                FirebaseDatabase.getInstance().reference.child("Chats").child(myUid).child(user.uid!!).push().setValue(messageData)
                if (myUid != user.uid){
                FirebaseDatabase.getInstance().reference.child("Chats").child(user.uid!!).child(myUid).push().setValue(messageData)
                }
                chat_log_edit_message.setText("")

                FirebaseDatabase.getInstance().reference.child("LatestMessage").child(myUid).child(user.uid!!).setValue(messageData)
                FirebaseDatabase.getInstance().reference.child("LatestMessage").child(user.uid!!).child(myUid).setValue(messageData)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        UserStatusController().userStatusWriter("в сети")
    }

    override fun onPause() {
        super.onPause()
        UserStatusController().userStatusWriter("offline")
    }
}
