package com.example.chatapp.lifecycleobservers

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.chatapp.controllers.UserStatusController
import com.example.chatapp.view.ChatLogActivity

class ChatLogLifecycleObserver: LifecycleObserver {
    val handler = Handler()

    @OnLifecycleEvent(Lifecycle.Event.ON_START )
    fun startOnlineStatusListener(){
        handler.postDelayed({
            UserStatusController().userStatusWriter("online")
        },1200)
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME )
    fun resumeOnlineStatusListener(){
            UserStatusController().userStatusWriter("online")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE )
    fun offlineStatusListener(){
        UserStatusController().userStatusWriter("offline")
        ChatLogActivity.userUid = ""
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY )
    fun offlineDestroyStatusListener(){
        UserStatusController().userStatusWriter("offline")
    }

}