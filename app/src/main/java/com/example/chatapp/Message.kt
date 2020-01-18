package com.example.chatapp



class Message(_name: String?, _text: String, _time:String?, _uid:String, _toUid: String) {
constructor():this("","","","","")
    var name: String?
    var text: String
    var time : String?
    var uid: String
    var toUid: String
    init {
        name = _name
        text = _text
        time = _time
        uid = _uid
        toUid = _toUid

    }
}