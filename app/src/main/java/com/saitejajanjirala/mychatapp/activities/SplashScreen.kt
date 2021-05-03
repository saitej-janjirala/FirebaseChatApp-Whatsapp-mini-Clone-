package com.saitejajanjirala.mychatapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import kotlinx.android.synthetic.main.activity_splash_screen.*
import okhttp3.internal.Util

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val image = findViewById<ImageView>(R.id.splashimage)
        val text = findViewById<TextView>(R.id.splashtext)
        val animation1 =
            AnimationUtils.loadAnimation(applicationContext, R.anim.splashimageanimation)
        image.startAnimation(animation1)
        val animation2 =
            AnimationUtils.loadAnimation(applicationContext, R.anim.splashtextanimation)
        text.startAnimation(animation2)
        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                progress_layout.visibility = View.VISIBLE
                val db = FirebaseFirestore.getInstance()
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                uid?.let {
                    db.collection(Keys.USERS).document(it).get()
                        .addOnSuccessListener { docSnap ->
                            progress_layout.visibility = View.GONE
                            val userName = docSnap.get(Keys.USER_NAME)
                            if (userName == null) {
                                startActivity(
                                    Intent(
                                        this@SplashScreen,
                                        EnternameActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                Utils.setOnline()
                                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener { ex ->
                            progress_layout.visibility = View.GONE
                            Toast.makeText(this@SplashScreen, ex.message, Toast.LENGTH_LONG).show()

                        }

                }
            } else {

                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            }
        }, 1600)
    }
}
