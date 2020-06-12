package com.saitejajanjirala.mychatapp.notifications

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceId :FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val pref=getSharedPreferences("user", Context.MODE_PRIVATE)
        val refreshtoken=FirebaseInstanceId.getInstance().instanceId.result!!.token
        if(pref.contains("uid")){
            updatetoken(refreshtoken)
        }
    }
    private fun updatetoken(refreshtoken:String?){
        val pref=getSharedPreferences("user", Context.MODE_PRIVATE)
        val uid=pref.getString("uid","")!!
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token=Token(refreshtoken!!)
        ref.child(uid).setValue(token)
    }
}