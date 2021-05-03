package com.saitejajanjirala.mychatapp.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import kotlinx.android.synthetic.main.activity_otp.*

object Utils {
    fun showSnackBar(context: Context, view:View, message:String){
        Snackbar.make(view,message, Snackbar.LENGTH_LONG)
            .setAction("Close") { }
            .setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            .show()
    }
    fun getRef(senderuid: String,visiteduid: String):String{
        var ref = ""
        if (senderuid.compareTo(visiteduid) > 0) {
            ref = "$senderuid$visiteduid"
        } else {
            ref = "$visiteduid$senderuid"
        }
        return ref
    }
    fun setOnline(){
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance().collection(Keys.USERS)
                .document(it.uid)
                .update(Keys.ACTIVE,true)
        }

    }
    fun setOffline(){
        FirebaseAuth.getInstance().currentUser?.let {
            FirebaseFirestore.getInstance().collection(Keys.USERS)
                .document(it.uid)
                .update(Keys.ACTIVE, false)
        }
    }
}