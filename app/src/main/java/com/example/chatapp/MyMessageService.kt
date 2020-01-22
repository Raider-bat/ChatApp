package com.example.chatapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyMessageService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val title = p0.data.getValue("title")
        val body = p0.data.getValue("body")
        val uid = p0.data.getValue("uid")
        val phoneNum = p0.data.getValue("phone")
        val token = p0.data.getValue("us_token")

        if (FirebaseAuth.getInstance().currentUser != null){
            NotificationHelper().displayNotify(applicationContext, title, body,uid,token,phoneNum)
        }
    }
}