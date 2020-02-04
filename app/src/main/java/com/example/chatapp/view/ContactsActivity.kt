package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Items.ContactItem
import com.example.chatapp.R
import com.example.chatapp.controllers.UserStatusController
import com.example.chatapp.data.User
import com.example.chatapp.data.UserStatus
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class ContactsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    val adapter = GroupAdapter<GroupieViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        supportActionBar?.title = "Контакты"

        recyclerviewSetting()
        readContactsFromDB()
    }

    private fun recyclerviewSetting() {
        recyclerView = findViewById(R.id.recyclerview_contacts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val layoutManager  =  LinearLayoutManager(this)
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout =true

    }


    companion object{
        const val USER_KEY = "USER_KEY"
    }

    val contactList = HashMap<String, ContactItem>()
    fun updateContactList(){
        adapter.update(contactList.values.sortedBy { it.user.userStatus?.time })
    }
   private fun readContactsFromDB() {

       FirebaseDatabase.getInstance().reference.child("Users")
           .orderByChild("userName").addChildEventListener(object : ChildEventListener{
           override fun onCancelled(p0: DatabaseError) {}
           override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
           override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {
               val userStatus = dataSnapshot
                   .child("UserStatus")
                   .getValue(UserStatus::class.java)
               val user = dataSnapshot
                   .getValue(User::class.java) ?:return
               user.userStatus = userStatus
               contactList[dataSnapshot.key!!] =
                   ContactItem(user)
               updateContactList()
           }

           override fun onChildAdded(dataSnapshot: DataSnapshot, strings: String?) {
               println(strings)
               val userStatus = dataSnapshot
                   .child("UserStatus")
                   .getValue(UserStatus::class.java)

               val user = dataSnapshot.getValue(User::class.java) ?:return
               user.userStatus = userStatus

               if (user.userName != ""){
                   contactList[dataSnapshot.key!!] =
                       ContactItem(user)
                   updateContactList()
               }
               adapter.setOnItemClickListener { item, view ->
                   val userItem = item as ContactItem
                   val intent = Intent(view.context, ChatLogActivity::class.java)
                   intent.putExtra(USER_KEY, userItem.user)
                   startActivity(intent)
                   finish()
               }
           }
           override fun onChildRemoved(p0: DataSnapshot) {}
       })
   }

    override fun onStart() {
        super.onStart()
        UserStatusController().userStatusWriter("online")
    }

    override fun onPause() {
        super.onPause()
        UserStatusController().userStatusWriter("offline")
    }
}