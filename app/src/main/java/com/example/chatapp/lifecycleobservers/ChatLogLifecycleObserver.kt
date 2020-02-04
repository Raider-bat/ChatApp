package com.example.chatapp.lifecycleobservers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.chatapp.controllers.UserStatusController
import com.example.chatapp.view.ChatLogActivity

class ChatLogLifecycleObserver: LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START )
    fun onlineStartStatusListener(){
        UserStatusController().userStatusWriter("online")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME )
    fun onlineResumeStatusListener(){
        UserStatusController().userStatusWriter("online")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE )
    fun offlineStatusListener(){
        UserStatusController().userStatusWriter("offline")
        ChatLogActivity.userUid = ""
    }
}