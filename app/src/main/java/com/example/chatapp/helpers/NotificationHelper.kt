package com.example.chatapp.helpers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chatapp.R
import com.example.chatapp.model.User
import com.example.chatapp.view.ChatLogActivity
import com.example.chatapp.view.ContactsActivity
import java.util.*

class NotificationHelper {
    private val channelId ="mes_id"
    private val channelName = "mes_name"
    private val channelDesc = "mes_notif"

    fun displayNotify(context: Context,
                      title: String?,
                      body:String?,
                      uid: String?,
                      token: String?,
                      phoneNum: String?) {
       if (title !=null && body !=null ) {
          val messId= (Date().time/1000).toInt()
           val user = User(phoneNum, title, uid, token)
           val notificationManager = NotificationManagerCompat.from(context)
           val notifyIntent = Intent(context, ChatLogActivity::class.java)
           notifyIntent.putExtra(ContactsActivity.USER_KEY, user)
           val contentIntent = PendingIntent.getActivity(
               context, 0, notifyIntent,
               PendingIntent.FLAG_CANCEL_CURRENT
           )

           val builder = NotificationCompat.Builder(context, channelId)
               .setSmallIcon(R.mipmap.ic_launcher_custom)
               .setContentTitle(title)
               .setContentText(body)
               .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               .setContentIntent(contentIntent)
               .setAutoCancel(true)


           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               val mChannel = NotificationChannel(
                   channelId,
                   channelName, NotificationManager.IMPORTANCE_HIGH
               )
               mChannel.description = channelDesc
               notificationManager?.createNotificationChannel(mChannel)
               notificationManager?.cancel(messId)
           }
           notificationManager.notify(messId, builder.build())
       }
   }
}