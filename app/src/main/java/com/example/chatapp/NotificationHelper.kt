package com.example.chatapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class NotificationHelper {
    val channel_id ="mes_id"
    val channel_name = "mes_name"
    val Channel_desc = "mes_notif"



    fun displayNotify(context: Context, title: String?, body:String?, uid: String?, token: String?, phoneNum: String?) {
       if (title !=null && body !=null ) {
          val messId= (Date().time/1000).toInt()
           val user = User(phoneNum, title, uid, token)
           val notificationManager = NotificationManagerCompat.from(context)
           val nofitIntent = Intent(context, ChatLogActivity::class.java)
           nofitIntent.putExtra(ContactsActivity.USER_KEY, user)
           val contentIntent = PendingIntent.getActivity(
               context, 0, nofitIntent,
               PendingIntent.FLAG_CANCEL_CURRENT
           )

           val builder = NotificationCompat.Builder(context, channel_id)
               .setSmallIcon(R.mipmap.ic_launcher_custom)
               .setContentTitle(title)
               .setContentText(body)
               .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               .setContentIntent(contentIntent)
               .setAutoCancel(true)


           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               val mChannel = NotificationChannel(
                   channel_id,
                   channel_name, NotificationManager.IMPORTANCE_HIGH
               )
               mChannel.description = Channel_desc
               notificationManager?.createNotificationChannel(mChannel)
               notificationManager?.cancel(messId)
           }
           notificationManager.notify(messId, builder.build())
       }
   }
}