package com.saitejajanjirala.mychatapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.saitejajanjirala.mychatapp.activities.Messagechat
import java.security.Provider

class MyFirebaseMessaging: FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val sented=p0.data["sented"]
        val user=p0.data["user"]
        if(FirebaseAuth.getInstance().currentUser!=null){
            val uid=FirebaseAuth.getInstance().currentUser.uid
            if(uid==sented){
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    sendoreonotification(p0)
                }
                else{
                    sendnotification(p0)
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendoreonotification(mremotemessage:RemoteMessage){
        val user=mremotemessage.data["user"]
        val icon=mremotemessage.data["icon"]
        val title=mremotemessage.data["title"]
        val body=mremotemessage.data["body"]
        val notification=mremotemessage.notification
        var j=user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent=Intent(this,Messagechat::class.java)
        val bundle=Bundle()
        bundle.putString("uid",user)
        intent.putExtras(bundle)
        val pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultsound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val oreoNotification=OreoNotification(this)
        val builder:Notification.Builder=oreoNotification.getoreonotification(title,body,pendingIntent,defaultsound,icon!!)
        var i=0
        if(j>0){
            j=i
        }
        oreoNotification.getmanager!!.notify(i,builder.build())
    }
    private fun sendnotification(mremotemessage: RemoteMessage){
        val user=mremotemessage.data["user"]
        val icon=mremotemessage.data["icon"]
        val title=mremotemessage.data["title"]
        val body=mremotemessage.data["body"]
        val notification=mremotemessage.notification
        var j=user!!.replace("[\\D]".toRegex(),"").toInt()
        val intent=Intent(this,Messagechat::class.java)
        val bundle=Bundle()
        bundle.putString("uid",user)
        intent.putExtras(bundle)
        val pendingIntent=PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
        val defaultsound=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder:NotificationCompat.Builder=NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultsound)
            .setContentIntent(pendingIntent)
        val notifi=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i=0
        if(j>0){
            j=i
        }
        notifi.notify(i,builder.build())
    }
}