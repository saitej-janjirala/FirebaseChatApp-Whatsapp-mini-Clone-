package com.saitejajanjirala.mychatapp.notifications

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.saitejajanjirala.mychatapp.utils.Keys

class MyFirebaseInstanceId :FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val refreshtoken=FirebaseInstanceId.getInstance().instanceId.result!!.token
        if(FirebaseAuth.getInstance().currentUser!=null){
            updatetoken(refreshtoken)

        }
    }
    private fun updatetoken(refreshtoken:String?){
        val uid= FirebaseAuth.getInstance().currentUser?.uid
        val ref= uid?.let { FirebaseFirestore.getInstance().collection(Keys.USERS).document(it) }
        ref?.update(Keys.FCM_TOKEN,refreshtoken)
    }
}