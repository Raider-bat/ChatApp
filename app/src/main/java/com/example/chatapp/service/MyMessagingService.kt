package com.example.chatapp.service

import com.example.chatapp.helpers.NotificationHelper
import com.example.chatapp.view.ChatLogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyMessagingService : FirebaseMessagingService() {
    companion object{
       var  notifyUid = ""
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val title = p0.data.getValue("title")
        val body = p0.data.getValue("body")
        val uid = p0.data.getValue("uid")
        val phoneNum = p0.data.getValue("phone")
        val token = p0.data.getValue("us_token")
        notifyUid = uid
        if (FirebaseAuth.getInstance().currentUser != null && ChatLogActivity.userUid != uid){
            NotificationHelper()
                .displayNotify(applicationContext, title, body,uid,token,phoneNum)
        }
    }
}