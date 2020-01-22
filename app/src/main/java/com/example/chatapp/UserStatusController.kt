package com.example.chatapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class UserStatusController {

    public fun userStatusWriter(state:String){
        val myUid = FirebaseAuth.getInstance().uid?:return
        val time = Date().time

        FirebaseDatabase.getInstance().reference.child("Users").child(myUid).child("UserStatus").setValue(UserStatus(
            time,state))
    }
}