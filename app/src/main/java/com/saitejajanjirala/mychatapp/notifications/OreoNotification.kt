package com.saitejajanjirala.mychatapp.notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.icu.text.CaseMap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class OreoNotification(base:Context?) :ContextWrapper(base){
    private var notificationManager:NotificationManager?= null
    companion object{
        private const val CHANNEL_ID="com.saitejajanjirala.mychatapp"
        private const val CHANNEL_NAME="Messenger App"
    }
    init{
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createchannel()
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private fun createchannel(){
        val channel=NotificationChannel(
            CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility=Notification.VISIBILITY_PRIVATE
        getmanager!!.createNotificationChannel(channel)
    }
    val getmanager:NotificationManager?
        get() {
            if(notificationManager==null){
                notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return notificationManager
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getoreonotification(title: String?, body:String?, pendingintent:PendingIntent?, sounduri:Uri?, icon:String):Notification.Builder{
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingintent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(icon!!.toInt())
            .setSound(sounduri)
            .setAutoCancel(true)
    }
}