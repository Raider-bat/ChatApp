package com.example.chatapp.helpers

import android.util.Log
import com.example.chatapp.data.UserStatus
import java.text.SimpleDateFormat
import java.util.*

class UserStatusHelper {

    fun userStatusPrinter(userStatus: UserStatus) : String {
        val timeDiff =((SimpleDateFormat("d").format(Date().time).toInt())
                - (SimpleDateFormat("d").format(userStatus.time).toInt()))
        Log.d("TIME", timeDiff.toString())
      return  when {
            userStatus.state == "online" -> {
                "в сети"
            }
            timeDiff == 1 -> {
                "был(а) вчера в " + SimpleDateFormat("HH:mm").format(userStatus.time)
            }
            timeDiff == 0 -> {
                "был(а) в "+ SimpleDateFormat("HH:mm").format(userStatus.time)
            }
            else -> {
                "был(а) " + SimpleDateFormat("d MMM в HH:mm").format(userStatus.time)
            }
        }
    }

}