package com.example.chatapp.lifecycleobservers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.chatapp.controllers.UserStatusController

class MainLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START )
    fun startOnlineStatusListener(){
        UserStatusController().userStatusWriter("online")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME )
    fun resumeOnlineStatusListener(){
        UserStatusController().userStatusWriter("online")
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