package com.example.chatapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val phoneNum:String?,val userName:String?, val uid: String?, val token: String?) : Parcelable {
    constructor():this("","","","")

}