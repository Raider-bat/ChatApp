package com.example.chatapp.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.items.ContactItem
import com.example.chatapp.model.User
import com.example.chatapp.model.UserStatus
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.example.chatapp.R
import com.example.chatapp.lifecycleobservers.ContactsLifecycleObserver


class ContactsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    val contactList = HashMap<String, ContactItem>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    val handler = Handler()
    lateinit var myLifecycleObserver: LifecycleObserver

    companion object{
        const val USER_KEY = "USER_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        supportActionBar?.title = "Контакты"

        myLifecycleObserver = ContactsLifecycleObserver()
        lifecycle.addObserver(myLifecycleObserver)
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



    fun updateContactList(){
             handler.postDelayed({
            adapter.update(contactList.values.sortedBy { it.user.userStatus?.time })
       }, 1500)
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
               contactList[dataSnapshot.key!!] = ContactItem(user)
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
}