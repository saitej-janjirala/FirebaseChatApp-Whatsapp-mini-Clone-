package com.saitejajanjirala.mychatapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class Connectivity(val context:Context) {
    fun checkConnectivity():Boolean{
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activenetworkinfo:NetworkInfo ?= connectivityManager.activeNetworkInfo
        if(activenetworkinfo?.isConnected!=null){
            return activenetworkinfo.isConnected
        }
        else{
            return false
        }
    }
    fun showDialog(){
            val dialog= AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("No Internet Connection")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings"){
                    text,listener->
                val intent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                context.startActivity(intent)
            }
            dialog.setNegativeButton("exit"){
                    text,listener->
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
    }
}