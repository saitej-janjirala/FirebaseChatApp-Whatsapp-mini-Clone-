package com.saitejajanjirala.mychatapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.activities.Messagechat
import com.saitejajanjirala.mychatapp.model.Chat
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class UserAdapter(val context: Context, val arraylist: ArrayList<User>, val ischatcheck: Boolean) :
    RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {
    var lastmessage: String? = null

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val profileimage: CircleImageView = view.findViewById(R.id.profileimagitem)
        val online: CircleImageView = view.findViewById(R.id.imageonline)
        val offline: CircleImageView = view.findViewById(R.id.imageoffline)
        val username: TextView = view.findViewById(R.id.usernameitem)
        val lastmessage: TextView = view.findViewById(R.id.lastchatmessage)
        val unseenCount:TextView=view.findViewById(R.id.unseen_count)
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
            retrievelastmessage(obj.uid, holder.lastmessage)
            getUnseenCount(obj.uid,holder.unseenCount)
        } else {
            holder.lastmessage.visibility = View.GONE
        }
        holder.username.text = obj.username
        if(obj.picurl==null){
            obj.picurl="https://cdn1.iconfinder.com/data/icons/business-office-and-internet-3-4/48/129-512.png"
        }
        Picasso.get().load(obj.picurl).error(R.drawable.ic_person)
            .placeholder(R.drawable.ic_person)
            .into(holder.profileimage)
        holder.view.setOnClickListener {
            val uid = obj.uid
            val intent = Intent(context, Messagechat::class.java)
            intent.putExtra("uid", uid)
            context.startActivity(intent)
        }

    }
    private fun getUnseenCount(chatUserId:String?,unseenCountText:TextView){
        val uid = FirebaseAuth.getInstance().currentUser.uid
        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(Utils.getRef(chatUserId!!,uid))
            .collection(Keys.MESSAGES)
            .whereEqualTo(Keys.RECEIVER_ID,uid)
            .whereEqualTo(Keys.DELETED,false)
            .whereEqualTo(Keys.SEEN,false)
            .addSnapshotListener(object:EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error==null){
                        value?.let {
                            val size=it.documents.size
                            if(size>0){
                                unseenCountText.visibility=View.VISIBLE
                                unseenCountText.text="$size"
                            }
                            else{
                                unseenCountText.visibility=View.GONE

                            }
                        }
                    }
                }

            })

    }
    private fun retrievelastmessage(chatuseruid: String?, lastmessagetext: TextView?) {
        lastmessage = "defaultmsg"
        val uid = FirebaseAuth.getInstance().currentUser.uid
        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(Utils.getRef(chatuseruid!!,uid))
            .collection(Keys.MESSAGES)
            .orderBy(Keys.TIME_STAMP,Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error==null){
                        value?.let {
                            if(it.documents.size>0){
                                it.documents[0].toObject(Chat::class.java)?.let { lastChat->
                                    if(lastChat.imageurl!=null && lastChat.senderid==uid){
                                        lastmessagetext!!.text="you sent an image"
                                    }else {
                                        lastmessagetext!!.text = lastChat.message
                                    }
                                }

                            }
                        }
                    }
                }

            })
    }

}