package com.saitejajanjirala.mychatapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.storage.StorageReference
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.models.Users
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
    var user: Users? = null
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
        supportActionBar?.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        val ref = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(Users::class.java)
                username.text = user!!.getusername()
                Picasso.get().load(user!!.getprofileurl()).error(R.drawable.ic_person)
                    .into(profileimage)
                Picasso.get().load(user!!.getcoverurl()).error(R.drawable.ic_person)
                    .into(coverimage)
            }
        })
        facebook.setOnClickListener {
            val uri = Uri.parse(user!!.getfacebook())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        instagram.setOnClickListener {
            val uri = Uri.parse(user!!.getinstagram())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        website.setOnClickListener {
            val uri = Uri.parse(user!!.getwebsite())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        sendmessage.setOnClickListener {
            val intent = Intent(this@VisituserprofileActivity, Messagechat::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
            finish()
        }
        coverimage.setOnClickListener {
            val intent = Intent(this, ViewfullimageActivity::class.java)
            intent.putExtra("url", user!!.getcoverurl())
            startActivity(intent)
        }
        profileimage.setOnClickListener {
            val intent = Intent(this, ViewfullimageActivity::class.java)
            intent.putExtra("url", user!!.getprofileurl())
            startActivity(intent)
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
}