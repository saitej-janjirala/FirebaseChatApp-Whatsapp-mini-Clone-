package com.saitejajanjirala.mychatapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId

import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.UserAdapter
import com.saitejajanjirala.mychatapp.models.ChatList
import com.saitejajanjirala.mychatapp.models.Users
import com.saitejajanjirala.mychatapp.notifications.Token

/**
 * A simple [Fragment] subclass.
 */
class ChatsFrgment : Fragment() {
    private lateinit var recyclerview:RecyclerView
    private lateinit var uid:String
    private lateinit var musers:ArrayList<Users>
    private lateinit var userschatlist:ArrayList<ChatList>
    private lateinit var useradapter:UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chats_frgment, container, false)
        uid=requireContext().getSharedPreferences("user",Context.MODE_PRIVATE).getString("uid","").toString()
        recyclerview=view.findViewById(R.id.recyclerviewchatlist)
        val dbref=FirebaseDatabase.getInstance().reference.child("chatlist").child(uid)
        userschatlist=ArrayList()
        dbref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                userschatlist.clear()
                p0.children.forEach {
                    val chatlist=it.getValue(ChatList::class.java)
                    userschatlist.add(chatlist!!)
                }
            }
        })
        retrievechatlist()
        updatetoken(FirebaseInstanceId.getInstance().token)
        return view
    }
    fun updatetoken(token:String?){
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1=Token(token!!)
        ref.child(uid).setValue(token1)
    }
    fun retrievechatlist(){
        musers= ArrayList()
        val ref=FirebaseDatabase.getInstance().reference.child("users")
        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError){
            }
            override fun onDataChange(p0: DataSnapshot) {
                musers.clear()
                p0.children.forEach {
                    val user=it.getValue(Users::class.java)
                    for(i in userschatlist){
                        if(user!!.getuid().equals(i.getid())){
                            musers.add(user)
                        }
                    }
                }
                Log.i("chatslist",musers.toString())
                 useradapter=UserAdapter(context!!,musers,true)
                recyclerview.setHasFixedSize(true)
                val layoutManager=LinearLayoutManager(context)
                layoutManager.orientation=LinearLayoutManager.VERTICAL
                recyclerview.addItemDecoration(DividerItemDecoration(recyclerview.context,layoutManager.orientation))
                recyclerview.layoutManager=layoutManager
                recyclerview.adapter=useradapter
            }
        })

    }

}
