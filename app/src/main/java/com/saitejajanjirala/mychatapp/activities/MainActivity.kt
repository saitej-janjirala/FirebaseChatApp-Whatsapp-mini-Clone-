package com.saitejajanjirala.mychatapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.ViewPagerAdapter
import com.saitejajanjirala.mychatapp.fragments.ChatsFrgment
import com.saitejajanjirala.mychatapp.fragments.SearchFragment
import com.saitejajanjirala.mychatapp.fragments.SettingsFragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.saitejajanjirala.mychatapp.models.Chat
import com.saitejajanjirala.mychatapp.models.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    lateinit var toolbar:Toolbar
    lateinit var tablayout:TabLayout
    lateinit var viewpager:ViewPager
    lateinit var madapter:ViewPagerAdapter
    lateinit var uid:String
    lateinit var username:TextView
    lateinit var profileimage:CircleImageView
    lateinit var refusers:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar=findViewById(R.id.toolbar_main)
        tablayout=findViewById(R.id.tab_layout)
        viewpager=findViewById(R.id.view_pager)
        username=findViewById(R.id.username)
        profileimage=findViewById(R.id.profile_image)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        uid= getSharedPreferences("user", Context.MODE_PRIVATE)!!.getString("uid","").toString()
            val ref = FirebaseDatabase.getInstance().reference.child("chats")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    val viewpageradapter=ViewPagerAdapter(this@MainActivity,supportFragmentManager)
                    var countunread = 0
                    p0.children.forEach {
                        val chat = it.getValue(Chat::class.java)
                        if (chat!!.getreciever().equals(uid) && chat.getisseen()!!.not()) {
                            countunread += 1
                        }
                    }
                    if (countunread==0){
                        viewpageradapter.addfragment(ChatsFrgment(),"Chats")
                    }
                    else{
                        viewpageradapter.addfragment(ChatsFrgment(),"($countunread)Chats")
                    }
                    viewpageradapter.addfragment(SearchFragment(),"Search")
                    viewpageradapter.addfragment(SettingsFragment(),"Settings")
                    viewpager.adapter=viewpageradapter
                    tablayout.setupWithViewPager(viewpager)
                }
            })
        refusers=FirebaseDatabase.getInstance().reference.child("users").child(uid)
        refusers.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    try{
                            val user=p0.getValue(Users::class.java)
                            username.text=user!!.getusername()
                            Picasso.get().load(user.getprofileurl()).error(R.drawable.ic_person).into(profileimage)

                    }catch (e:Exception){
                        Log.i("error",e.message.toString())
                    }
                }
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this@MainActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout->{
                val alertDialog=AlertDialog.Builder(this@MainActivity)
                alertDialog.setTitle("Confirmation")
                alertDialog.setMessage("Are you sure you want to Logout?")
                alertDialog.setNegativeButton("no"){text,listener->
                }
                alertDialog.setPositiveButton("yes"){text,listener->
                    val sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()
                    startActivity(Intent(this@MainActivity,LoginActivity::class.java))
                }
                alertDialog.create()
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
