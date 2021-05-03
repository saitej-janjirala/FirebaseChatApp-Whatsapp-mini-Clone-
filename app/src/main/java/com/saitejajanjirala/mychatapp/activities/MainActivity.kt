package com.saitejajanjirala.mychatapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.ViewPagerAdapter
import com.saitejajanjirala.mychatapp.fragments.ChatsFragment
import com.saitejajanjirala.mychatapp.fragments.SearchFragment
import com.saitejajanjirala.mychatapp.fragments.SettingsFragment
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_entername.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var madapter: ViewPagerAdapter
    lateinit var uid: String
    lateinit var username: TextView
    lateinit var profileimage: CircleImageView
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        renderInitials()
        setAdapters()
        logout_button.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@MainActivity)
            alertDialog.setTitle("Confirmation")
            alertDialog.setMessage("Are you sure you want to Logout?")
            alertDialog.setNegativeButton("no") { text, listener ->
            }
            alertDialog.setPositiveButton("yes") { text, listener ->
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            alertDialog.create()
            alertDialog.show()
        }
    }

    private fun renderInitials() {
        uid = FirebaseAuth.getInstance().currentUser.uid
        db = FirebaseFirestore.getInstance()
        db.collection(Keys.USERS).document(uid)
            .addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (error == null) {
                        value?.let {
                            val user = it.toObject(User::class.java)
                            Log.i("document", "$user")
                            Glide.with(this@MainActivity)
                                .load(it[Keys.PIC_URL])
                                .error(R.drawable.ic_person)
                                .into(profile_image)
                            if (it[Keys.USER_NAME] != null) {
                                user_name.text = it[Keys.USER_NAME] as String
                            }

                        }
                    }
                }

            })
    }

    private fun setAdapters() {
        madapter = ViewPagerAdapter(this@MainActivity, supportFragmentManager)
        madapter.addfragment(ChatsFragment(), "Chats")
        madapter.addfragment(SearchFragment(), "Search")
        madapter.addfragment(SettingsFragment(), "Settings")
        view_pager.adapter = madapter
        tab_layout.setupWithViewPager(view_pager)
    }
//        toolbar = findViewById(R.id.toolbar_main)
//        tablayout = findViewById(R.id.tab_layout)
//        viewpager = findViewById(R.id.view_pager)
//        username = findViewById(R.id.username)
//        profileimage = findViewById(R.id.profile_image)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.title = ""
//        uid = FirebaseAuth.getInstance().currentUser!!.uid
//        val ref = FirebaseDatabase.getInstance().reference.child("chats")
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {

//                var countunread = 0
//                p0.children.forEach {
//                    val chat = it.getValue(Chat::class.java)
//                    if (chat!!.getreciever().equals(uid) && chat.getisseen()!!.not()) {
//                        countunread += 1
//                    }
//                }
//                if (countunread == 0) {
//                    viewpageradapter.addfragment(ChatsFragment(), "Chats")
//                } else {
//                    viewpageradapter.addfragment(ChatsFragment(), "($countunread)Chats")
//                }

//            }
//        })
//        refusers = FirebaseDatabase.getInstance().reference.child("users").child(uid)
//        refusers.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                if (p0.exists()) {
//                    try {
//                        val user = p0.getValue(Users::class.java)
//                        username.text = user!!.getusername()
//                        Picasso.get().load(user.getprofileurl()).error(R.drawable.ic_person)
//                            .into(profileimage)
//
//                    } catch (e: Exception) {
//                        Log.i("error", e.message.toString())
//                    }
//                }
//            }
//        })
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this@MainActivity)
    }
    override fun onStart() {
        super.onStart()
        Utils.setOnline()
    }

    override fun onStop() {
        super.onStop()
        Utils.setOffline()
    }


}
