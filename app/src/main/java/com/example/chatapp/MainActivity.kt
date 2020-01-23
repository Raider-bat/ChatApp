package com.example.chatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
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
            }
        }
        return super.onOptionsItemSelected(item)
    }
    lateinit var recyclerView: RecyclerView
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(FirebaseAuth.getInstance().currentUser == null ){
            val intent = Intent(this, LoginActivity::class.java1)
            startActivity(intent)
            finish()
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
               if (p0.value == null){
                   val intent = Intent(this@MainActivity, LoginActivity::class.java1)

                   startActivity(intent)
                   finish()
               }
            }
        })

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
        listenLastMessage()
    }

    val lastMessageMap = HashMap<String, Message>()
    fun refreshRecyclerviewMessage(){
        adapter.clear()

        lastMessageMap.values.forEach{
            adapter.add(LatestMessageItem(it))
        }
    }
    private fun listenLastMessage() {
        val myUid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("LatestMessage").child(myUid!!).addChildEventListener(object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val lastMes = p0.getValue(Message::class.java1) ?:return
               // adapter.add(LatestMessageItem(lastMes))
                lastMessageMap[p0.key!!] = lastMes
               refreshRecyclerviewMessage()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val lastMes = p0.getValue(Message::class.java1) ?:return
                //adapter.add(LatestMessageItem(lastMes))
                lastMessageMap[p0.key!!] = lastMes
                refreshRecyclerviewMessage()
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this, LoginActivity::class.java1)

            startActivity(intent)
            finish()
        }

        FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().uid!!).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null){
                    finish()
                }
            }
        })
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