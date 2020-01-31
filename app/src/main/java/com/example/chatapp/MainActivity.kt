package com.example.chatapp


import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap
import kotlin.jvm.java as java1

class MainActivity : AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when(item.itemId){
                R.id.menu_sign_out ->{
                    UserStatusController().userStatusWriter("offline")
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        val intent = Intent(this, LoginActivity::class.java1)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }

                R.id.menu_contacts ->{
                    val intent = Intent(this, ContactsActivity::class.java1)
                    startActivity(intent)
                }
                R.id.menu_change_name ->{
                    val uid = FirebaseAuth.getInstance().uid
                    if (uid != null){
                        val intent = Intent(this, AddNameUserActivity::class.java1)
                        intent.putExtra(LoginActivity.USER_UID,uid)
                        startActivity(intent)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var recyclerView: RecyclerView
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val lastMessageMap = HashMap<String, LatestMessageItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserVerification()
        recyclerviewSetting()
        listenLastMessage()
    }

    private fun checkUserVerification() {
        if(FirebaseAuth.getInstance().currentUser == null ){
            val intent = Intent(this, LoginActivity::class.java1)
            startActivity(intent)
            finish()
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).child("userName").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null){
                    AuthUI.getInstance().signOut(this@MainActivity)
                    val intent = Intent(this@MainActivity, LoginActivity::class.java1)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }


    private fun recyclerviewSetting(){
        recyclerView = findViewById(R.id.latest_mes_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.isSmoothScrollbarEnabled = true
        layoutManager.reverseLayout =true
        recyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val userData =  item as LatestMessageItem
            val intent = Intent(this, ChatLogActivity::class.java1)
            intent.putExtra(ContactsActivity.USER_KEY, userData.partUser)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    fun updateRecyclerviewMessage(){
        adapter.update(lastMessageMap.values.sortedBy { it.message.time })
    }

    private fun listenLastMessage() {
        val myUid = FirebaseAuth.getInstance().uid ?:return
        FirebaseDatabase.getInstance().reference.child("LatestMessage").child(myUid).orderByChild("time").addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val lastMes = p0.getValue(Message::class.java1) ?:return
                lastMessageMap[p0.key!!] = LatestMessageItem(lastMes)
                updateRecyclerviewMessage()
                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount-1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val lastMes = p0.getValue(Message::class.java1) ?:return
                lastMessageMap[p0.key!!] = LatestMessageItem(lastMes)
                updateRecyclerviewMessage()
                recyclerView.smoothScrollToPosition(recyclerView.adapter!!.itemCount-1)
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    override fun onStart() {
        super.onStart()
        checkUserVerification()
        UserStatusController().userStatusWriter("в сети")
    }

    override fun onPause() {
        super.onPause()
        UserStatusController().userStatusWriter("offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        UserStatusController().userStatusWriter("offline")
    }
}