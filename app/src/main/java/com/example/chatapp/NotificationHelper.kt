package com.example.chatapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat.getSystemService

 class NotificationHelper {
    val channel_id ="mes_id"
    val channel_name = "mes_name"
    val Channel_desc = "mes_notif"
     var i = 0

   @Suppress("DEPRECATION")
   public fun displayNotify(context: Context, title: String?, body:String?, uid: String?, token: String?, phoneNum: String?) {


       var mesStyle = NotificationCompat.MessagingStyle(title!!)
       mesStyle.addMessage(body, System.currentTimeMillis(), title)
       val user: User = User(phoneNum,title,uid,token)

       val nofitIntent = Intent(context, ChatLogActivity::class.java)
       nofitIntent.putExtra(ContactsActivity.USER_KEY, user)
       val contentIntent = PendingIntent.getActivity(
           context, 0, nofitIntent,
           PendingIntent.FLAG_CANCEL_CURRENT
       )

       val builder = NotificationCompat.Builder(context, channel_id)
           .setSmallIcon(R.mipmap.ic_launcher_custom)
           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           .setContentIntent(contentIntent)
           .setStyle(mesStyle)
           .setAutoCancel(true)


       val notificationManager = NotificationManagerCompat.from(context)
       notificationManager.notify(i, builder.build())
       i++
   }









}