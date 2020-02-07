package com.example.chatapp.view


import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.model.Message
import com.example.chatapp.model.User
import com.example.chatapp.model.UserStatus
import com.example.chatapp.helpers.UserStatusHelper
import com.example.chatapp.items.MessageDateItem
import com.example.chatapp.items.MessageItem
import com.example.chatapp.items.MessageItemFrom
import com.example.chatapp.lifecycleobservers.ChatLogLifecycleObserver
import com.example.chatapp.lifecycleobservers.MyActionModeCallBack
import com.example.chatapp.service.MyMessagingService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    var adapter = GroupAdapter<GroupieViewHolder>()
    lateinit var user : User
    lateinit var  myLifecycleObserver : LifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
         user = intent.getParcelableExtra<User>(
             ContactsActivity.USER_KEY
         )
        supportActionBar?.title = user.userName
        userStatusAsActionBarSubTitle()
        recyclerviewSetting()
        myLifecycleObserver =
            ChatLogLifecycleObserver()
        lifecycle.addObserver(myLifecycleObserver)


        val notificationManager = ContextCompat
            .getSystemService(this,
            NotificationManager::class.java)
        if (MyMessagingService.notifyUid == user.uid) {
            notificationManager?.cancelAll()
        }

        emojiSetting()
        writeMessage()
        listMessage()
    }

    private fun recyclerviewSetting() {
        recyclerView = findViewById(R.id.chat_log_list_message)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun userStatusAsActionBarSubTitle() {
        FirebaseDatabase.getInstance().reference.child("Users")
            .child(user.uid!!)
            .child("UserStatus")
            .addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val userStatus = p0.getValue(UserStatus::class.java)?:return
                supportActionBar?.subtitle = UserStatusHelper()
                    .userStatusPrinter(userStatus)
            }
        })
    }

    private fun emojiSetting() {
        val emojiButton = findViewById<ImageButton>(R.id.emoji_button)
        val emojiIcomAction = EmojIconActions(this,
            chat_log_activity,
            chat_log_edit_message,
            emojiButton,
            "#777777",
            "#2C2C2C",
            "#2C2C2C")
        emojiIcomAction.setIconsIds(
            R.drawable.ic_keyboard_grey_500_24dp,
            R.drawable.ic_insert_emoticon_grey_500_24dp
        )
        emojiIcomAction.ShowEmojIcon()
    }

    private fun listMessage(){
        var dateLatestMessage = 0
        chat_activity_progress_bar.visibility = ProgressBar.VISIBLE
        val myUid = FirebaseAuth.getInstance().uid?:return
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(myUid)
            .child(user.uid!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.childrenCount == 0L) {
                    chat_activity_progress_bar.visibility = ProgressBar.INVISIBLE
                    text_view_no_messages.visibility = TextView.VISIBLE
                }
            }
        })
        FirebaseDatabase.getInstance().reference
            .child("Chats")
            .child(myUid)
            .child(user.uid!!)
            .addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                text_view_no_messages.visibility = TextView.INVISIBLE
                val mes = p0.getValue(Message::class.java) ?:return

                val dateForDialog = SimpleDateFormat("d MMMM")
                val dateNowMessage = SimpleDateFormat("d").format(mes.time).toInt()
                if (dateNowMessage - dateLatestMessage !=0){
                    adapter.add(
                        MessageDateItem(
                            dateForDialog.format(
                                mes.time
                            )
                        )
                    )
                }
                if (mes.uid == FirebaseAuth.getInstance().uid){
                    adapter.add(MessageItemFrom(mes))

                }else {
                    adapter.add(MessageItem(mes))
                }
                dateLatestMessage = SimpleDateFormat("d").format(mes.time).toInt()
                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)
                chat_activity_progress_bar.visibility = ProgressBar.INVISIBLE

            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        adapter.setOnItemLongClickListener { item, _ ->

            if (MyActionModeCallBack.mActionMode != null){
                return@setOnItemLongClickListener false
            }else {
                MyActionModeCallBack.mActionMode = startActionMode(
                    MyActionModeCallBack(
                        item,
                        adapter
                    )
                )
                return@setOnItemLongClickListener true
            }

        }

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
                val messageData = Message(
                    userNameInDB,
                    lEditText,
                    dataTime,
                    myUid,
                    user.uid!!
                )
                FirebaseDatabase.getInstance().reference
                    .child("Chats")
                    .child(myUid)
                    .child(user.uid!!)
                    .push()
                    .setValue(messageData)
                if (myUid != user.uid){
                FirebaseDatabase.getInstance().reference
                    .child("Chats")
                    .child(user.uid!!)
                    .child(myUid)
                    .push()
                    .setValue(messageData)
                }
                chat_log_edit_message.setText("")
                FirebaseDatabase.getInstance().reference.child("LatestMessage")
                    .child(myUid)
                    .child(user.uid!!)
                    .setValue(messageData)
                FirebaseDatabase.getInstance().reference.child("LatestMessage")
                    .child(user.uid!!)
                    .child(myUid)
                    .setValue(messageData)
            }
        }
    }

    companion object {
        var userUid: String = ""
    }

    override fun onStart() {
        super.onStart()
        userUid = user.uid!!
    }
}
