package com.example.chatapp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class MainLifecycleObserver : LifecycleObserver {



    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME )
    fun onlineStatusListener(){
        UserStatusController().userStatusWriter("в сети")

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