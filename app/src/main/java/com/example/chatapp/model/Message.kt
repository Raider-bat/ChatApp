package com.example.chatapp.model

data class Message(val name: String? = "",
                   val text: String = "",
                   val time: Long = 0,
                   val uid:String = "",
                   val toUid: String = "")
