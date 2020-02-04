package com.example.chatapp.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val phoneNum:String? = "",
    val userName:String? = "",
    val uid: String? = "",
    val token: String? = "",
    var userStatus: UserStatus? = null
) : Parcelable