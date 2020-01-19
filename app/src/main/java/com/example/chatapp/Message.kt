package com.example.chatapp



class Message(val name: String?, val text: String, val time: Long, val uid:String, val toUid: String) {
constructor():this("","",0,"","")
}