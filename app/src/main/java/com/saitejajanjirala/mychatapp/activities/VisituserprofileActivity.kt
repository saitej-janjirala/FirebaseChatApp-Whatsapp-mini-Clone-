package com.saitejajanjirala.mychatapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class VisituserprofileActivity : AppCompatActivity() {
    private lateinit var coverimage: ImageView
    private lateinit var profileimage: CircleImageView
    private lateinit var uid: String
    private lateinit var username: TextView
    private lateinit var mref: DatabaseReference
    private lateinit var facebook: CircleImageView
    private lateinit var instagram: CircleImageView
    private lateinit var website: CircleImageView
    private lateinit var sendmessage: Button
    private lateinit var usernumber:TextView
    var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visituserprofile)
        uid = intent.getStringExtra("uid")!!
        coverimage = findViewById(R.id.coverimagevisit)
        profileimage = findViewById(R.id.profileimagevisit)
        facebook = findViewById(R.id.facebookvisit)
        username = findViewById(R.id.usernamevisit)
        instagram = findViewById(R.id.instagramvisit)
        website = findViewById(R.id.webistevisit)
        sendmessage = findViewById(R.id.sendmessagevisit)
        usernumber=findViewById(R.id.user_number_visit)
        supportActionBar?.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        FirebaseFirestore.getInstance().collection(Keys.USERS)
            .document(uid)
            .addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error == null) {
                        value?.let {
                            user = it.toObject(User::class.java)
                            if (user != null) {
                                username.text = user!!.username
                                usernumber.text="${user!!.phonenumber?.substring(3)}"
                                Picasso.get().load(user!!.picurl)
                                    .placeholder(R.drawable.ic_person)
                                    .error(R.drawable.ic_person)
                                    .into(profileimage)
                                Picasso.get().load(user!!.coverurl)
                                    .placeholder(R.drawable.cover_image)
                                    .error(R.drawable.cover_image)
                                    .into(coverimage)
                            }
                        }
                    } else {
                        finish()
                    }
                }

            })
        facebook.setOnClickListener {
            user!!.facebook?.let {
                val uri = Uri.parse(it)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if(isActivityForIntentAvailable(this,intent)) {
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"facebook url is not correct",Toast.LENGTH_LONG).show()
                }
            }
        }
        instagram.setOnClickListener {
            user!!.instagram?.let {
                val uri = Uri.parse(it)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if(isActivityForIntentAvailable(this,intent)) {
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"instagram url is not correct",Toast.LENGTH_LONG).show()
                }
            }

        }
        website.setOnClickListener {
            user!!.email?.let {
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$it"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey i found you from Mini Whats app")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Lets connect")
                if(isActivityForIntentAvailable(this,intent)) {
                    startActivity(Intent.createChooser(emailIntent, "Choose the app"));
                }
                else{
                    Toast.makeText(this,"email is not correct",Toast.LENGTH_LONG).show()
                }

            }

        }
        sendmessage.setOnClickListener {
            val intent = Intent(this@VisituserprofileActivity, Messagechat::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
            finish()
        }
        coverimage.setOnClickListener {
            user?.coverurl?.let {
                val intent = Intent(this, ViewfullimageActivity::class.java)
                intent.putExtra("url", it)
                startActivity(intent)
            }

        }
        profileimage.setOnClickListener {
           user?.picurl?.let {
               val intent = Intent(this, ViewfullimageActivity::class.java)
               intent.putExtra("url", it)
               startActivity(intent)
           }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onStart() {
        super.onStart()
        Utils.setOnline()
    }
    @SuppressLint("QueryPermissionsNeeded")
    fun isActivityForIntentAvailable(
        context: Context,
        intent: Intent?
    ): Boolean {
        val packageManager = context.packageManager
        val list: List<*> =
            packageManager.queryIntentActivities(intent!!, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    override fun onStop() {
        super.onStop()
        Utils.setOffline()
    }
}