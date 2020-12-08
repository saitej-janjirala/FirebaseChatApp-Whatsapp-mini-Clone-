package com.saitejajanjirala.mychatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.activities.Messagechat
import com.saitejajanjirala.mychatapp.activities.VisituserprofileActivity
import com.saitejajanjirala.mychatapp.models.Chat
import com.saitejajanjirala.mychatapp.models.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(val context: Context, val arraylist: ArrayList<Users>, val ischatcheck: Boolean) :
    RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {
    var lastmessage: String? = null

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profileimage: CircleImageView = view.findViewById(R.id.profileimagitem)
        val online: CircleImageView = view.findViewById(R.id.imageonline)
        val offline: CircleImageView = view.findViewById(R.id.imageoffline)
        val username: TextView = view.findViewById(R.id.usernameitem)
        val lastmessage: TextView = view.findViewById(R.id.lastchatmessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.user_search_item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val obj = arraylist[position]
        if (ischatcheck) {
            retrievelastmessage(obj.getuid(), holder.lastmessage)
        } else {
            holder.lastmessage.visibility = View.GONE
        }

        holder.username.text = obj.getusername()
        Picasso.get().load(obj.getprofileurl()).error(R.drawable.ic_person)
            .into(holder.profileimage)
        holder.view.setOnClickListener {
            val uid = obj.getuid()
            val intent = Intent(context, Messagechat::class.java)
            intent.putExtra("uid", uid)
            context.startActivity(intent)
        }
    }

    private fun retrievelastmessage(chatuseruid: String?, lastmessagetext: TextView?) {
        lastmessage = "defaultmsg"
        val uid = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid", "")!!
        val fref = FirebaseDatabase.getInstance().reference.child("chats")
        fref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val chat: Chat? = it.getValue(Chat::class.java)
                    if (chat != null) {
                        if (chat.getreciever() == uid && chat.getsender() == chatuseruid || chat.getsender() == uid && chat.getreciever() == chatuseruid) {
                            lastmessage = chat.getmessage()!!
                        }
                    }
                }
                when (lastmessage) {
                    "defaultmsg" -> {
                        lastmessagetext!!.text = ""
                    }
                    "sent you an image." -> {
                        lastmessagetext!!.text = "sent an image"
                    }
                    else -> {
                        lastmessagetext!!.text = lastmessage
                    }

                }
                lastmessage = "deaultmsg"
            }
        })
    }

}