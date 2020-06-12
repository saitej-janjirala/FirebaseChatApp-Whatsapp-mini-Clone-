package com.saitejajanjirala.mychatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.saitejajanjirala.mychatapp.R
import android.content.Context

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val image=findViewById<ImageView>(R.id.splashimage)
        val text=findViewById<TextView>(R.id.splashtext)
        val animation1=AnimationUtils.loadAnimation(applicationContext,R.anim.splashimageanimation)
        image.startAnimation(animation1)
        val animation2=AnimationUtils.loadAnimation(applicationContext,R.anim.splashtextanimation)
        text.startAnimation(animation2)
        Handler().postDelayed({
            val sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
            if(sharedPreferences.contains("uid")){
                startActivity(Intent(this@SplashScreen,MainActivity::class.java))
            }
            else{
                startActivity(Intent(this@SplashScreen,LoginActivity::class.java))
            }
        },1600)
    }
}
