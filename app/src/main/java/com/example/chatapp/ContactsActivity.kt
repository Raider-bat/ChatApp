package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ContactsActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        supportActionBar?.title = "Контакты"

        recyclerView = findViewById(R.id.recyclerview_contacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager

        readContactsFromDB()
        recyclerView.adapter = adapter
    }

    companion object{
        val USER_KEY = "USER_KEY"
    }
   private fun readContactsFromDB() {

       FirebaseDatabase.getInstance().reference.child("Users").addChildEventListener(object : ChildEventListener{
           override fun onCancelled(p0: DatabaseError) {
           }
           override fun onChildMoved(p0: DataSnapshot, p1: String?) {
           }
           override fun onChildChanged(p0: DataSnapshot, p1: String?) {
           }

           override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               val user = p0.getValue(User::class.java)
               if (user != null){

                   adapter.add(ContactItem(user))
               }
               adapter.setOnItemClickListener { item, view ->
                   val userItem = item as ContactItem
                   val intent = Intent(view.context, ChatLogActivity::class.java)
                   intent.putExtra(ContactsActivity.USER_KEY, userItem.user)

                   startActivity(intent)
                   finish()
               }
           }
           override fun onChildRemoved(p0: DataSnapshot) {
           }
       })
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