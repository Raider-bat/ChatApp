package com.example.chatapp.controllers

import com.example.chatapp.data.UserStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserStatusController {

     fun userStatusWriter(state:String){
        val myUid = FirebaseAuth.getInstance().uid?:return
         var time = Date().time
         if (state =="online"){
             time = Date().time + Date().time
         }

        FirebaseDatabase.getInstance().reference.child("Users")
            .child(FirebaseAuth.getInstance().uid!!)
            .child("userName")
            .addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value != null){
                    FirebaseDatabase.getInstance().reference
                        .child("Users")
                        .child(myUid)
                        .child("UserStatus")
                        .setValue(
                            UserStatus(
                                time, state
                            )
                        )
                }
            }
        })
    }
}