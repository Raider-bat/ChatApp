package com.example.chatapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserStatus(
    val time: Long = 0L,
    val state: String = ""
) : Parcelable