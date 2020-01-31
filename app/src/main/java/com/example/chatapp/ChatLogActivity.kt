package com.example.chatapp


import android.app.NotificationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener
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
        recyclerviewSetting()

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java)
        if (MyMessageService.notifyUid == user.uid) {
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
        FirebaseDatabase.getInstance().reference.child("Users").child(user.uid!!).child("UserStatus").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userStatus = p0.getValue(UserStatus::class.java)?:return
                val dateNowMessage ="был(а) в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
                val timeDifference = (SimpleDateFormat("d").format(Date().time).toInt())- (SimpleDateFormat("d").format(userStatus.time).toInt())
                val statusNow = when {
                    userStatus.state =="в сети" -> {
                        userStatus.state
                    }
                    timeDifference ==1 -> {
                        "был(а) вчера в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
                    }
                    timeDifference  in 2..20 ->{
                        "был(а) " +SimpleDateFormat("d MMM в HH:mm").format(userStatus.time)
                    }
                    timeDifference > 20 -> {
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

    private fun listMessage(){
        var dateLatestMessage = 0
        val myUid = FirebaseAuth.getInstance().uid?:return
        FirebaseDatabase.getInstance().reference.child("Chats").child(myUid).child(user.uid!!).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val mes = p0.getValue(Message::class.java) ?:return

                val dateForDialog = SimpleDateFormat("d MMMM")
                val dateNowMessage = SimpleDateFormat("d").format(mes.time).toInt()
                if (dateNowMessage - dateLatestMessage !=0){
                    adapter.add(MessageDateItem(dateForDialog.format(mes.time)))
                }
                if (mes.uid == FirebaseAuth.getInstance().uid){
                    adapter.add(MessageItemFrom(mes))

                }else {
                    adapter.add(MessageItem(mes))
                }
                dateLatestMessage = SimpleDateFormat("d").format(mes.time).toInt()
                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount)

            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildRemoved(p0: DataSnapshot) {
               // adapter.removeGroupAtAdapterPosition(recyclerView.adapter!!.itemCount-1)
               // val mes = p0.getValue(Message::class.java)?:return
                // adapter.remove(MessageItem(mes))
               // if(mes.toUid != mes.uid){
                 //   if(mes.uid == user.uid!!){
                  //      MessageItem(mes).unbind(GroupieViewHolder(View(this@ChatLogActivity)))
                       // adapter.removeGroupAtAdapterPosition(MessageItem(mes).getPosition())
             //      }
             //    }
            }
        })

        adapter.setOnItemLongClickListener  { item, view ->

            val messageItem = item as MessageItemFrom
            val mes = messageItem.message

            val myUid = FirebaseAuth.getInstance().uid
            FirebaseDatabase.getInstance().reference.child("Chats").child(myUid!!)
                .child(mes!!.toUid)
                .orderByChild("time")
                .equalTo(mes.time.toDouble())
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach {
                            it.ref.removeValue()
                        }
                    }
                })
            if (myUid != mes.toUid) {
                FirebaseDatabase.getInstance().reference.child("Chats").child(mes!!.toUid)
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
            adapter.removeGroupAtAdapterPosition(adapter.getAdapterPosition(item))
            val pos = adapter.getAdapterPosition(item).toString()
            Log.d("WORKK", pos)
            true
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

    companion object {
        var userUid: String = ""
    }

    override fun onStart() {
        super.onStart()
        userUid = user.uid!!
        UserStatusController().userStatusWriter("в сети")
    }

    override fun onPause() {
        super.onPause()
        userUid = ""
        UserStatusController().userStatusWriter("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        userUid = ""
    }

    override fun onResume() {
        super.onResume()
        UserStatusController().userStatusWriter("в сети")
        userUid = user.uid!!
    }
}
