package com.example.chatapp.lifecycleobservers

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.chatapp.controllers.UserStatusController

class ContactsLifecycleObserver: LifecycleObserver {
    val handler  = Handler()
    @OnLifecycleEvent(Lifecycle.Event.ON_START )
    fun resumeOnlineStatusListener(){
        handler.postDelayed({
            UserStatusController().userStatusWriter("online")
        },200)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE )
    fun offlineStatusListener(){
        UserStatusController().userStatusWriter("offline")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY )
    fun offlineDestroyStatusListener(){
        UserStatusController().userStatusWriter("offline")
    }

}