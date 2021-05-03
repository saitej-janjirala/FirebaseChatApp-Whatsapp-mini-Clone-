package com.saitejajanjirala.mychatapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.activities.ViewfullimageActivity
import com.saitejajanjirala.mychatapp.model.Chat
import com.saitejajanjirala.mychatapp.models.Chats
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(val context:Context,val senderUid:String,val receiverUid:String, val chatlist:ArrayList<Chat>, val mimageurl:String?):
    RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {
    private var msenderuid:String?=null
    private var imageurl:String?=null
    init {
        msenderuid=senderUid
        imageurl=mimageurl
    }
    class ChatViewHolder(view:View):RecyclerView.ViewHolder(view){
        var profileimage:CircleImageView?=null
        var textmessage:TextView?=null
        var rightimageView:ImageView?=null
        var leftimageview:ImageView?=null
        var textseen:TextView?=null
        init{
            profileimage=view.findViewById(R.id.chatprofileimage)
            textmessage=view.findViewById(R.id.chatmessage)
            rightimageView=view.findViewById(R.id.chatimageright)
            leftimageview=view.findViewById(R.id.chatimageleft)
            textseen=view.findViewById(R.id.textseen)
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup,position: Int): ChatViewHolder {
        var viewholder:Any?=null
        return if(position==1) {
            val view = LayoutInflater.from(context).inflate(R.layout.messageitemright, parent, false)
            ChatViewHolder(view)
        }
        else{
            val view = LayoutInflater.from(context).inflate(R.layout.messageitemleft, parent, false)
            ChatViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatlist.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        try {
            Picasso.get()
                .load(imageurl)
                .error(R.drawable.ic_person)
                .placeholder(R.drawable.ic_person)
                .into(holder.profileimage)
            val chat = chatlist[position]

            if(chat.imageurl!=null){
                if (chat.senderid.equals(msenderuid)) {
                        holder.textmessage!!.visibility = View.GONE
                        holder.rightimageView!!.visibility = View.VISIBLE
                        Picasso.get().load(chat.imageurl).error(R.drawable.ic_person).into(holder.rightimageView)
                        holder.rightimageView!!.setOnClickListener {
                            val options=arrayOf<CharSequence>(
                                "view full image",
                                "delete image","cancel"
                            )
                            val builder= AlertDialog.Builder(holder.itemView.context)
                            builder.setTitle("What do you want?")
                            builder.setItems(options, DialogInterface.OnClickListener {
                            dialog,which->
                                when(which){
                                    0->{
                                        val intent= Intent(context, ViewfullimageActivity::class.java)
                                        intent.putExtra("url",chat.imageurl)
                                        context.startActivity(intent)
                                    }
                                    1->{
                                        deletesendmessage(position,holder)
                                    }
                                }
                            })
                            builder.create()
                            builder.show()
                        }
                } else {
                        holder.textmessage!!.visibility = View.GONE
                        holder.leftimageview!!.visibility = View.VISIBLE
                        Picasso.get().load(chat.imageurl).error(R.drawable.ic_person)
                            .into(holder.leftimageview)
                    holder.leftimageview!!.setOnClickListener {
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setTitle("What do you want?")
                        val options = arrayOf<CharSequence>(
                            "view full image",
                            "cancel"
                        )
                        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                0 -> {
                                    val intent = Intent(context, ViewfullimageActivity::class.java)
                                    intent.putExtra("url", chat.imageurl)
                                    context.startActivity(intent)
                                }
                            }
                        })
                        builder.create()
                        builder.show()
                    }

                }
            }
            else{
                holder.textmessage!!.text=chat.message
                if (chat.senderid.equals(msenderuid)) {
                    holder.textmessage!!.setOnClickListener {
                        val options = arrayOf<CharSequence>(
                            "delete message", "cancel"
                        )
                        val builder = AlertDialog.Builder(holder.itemView.context)
                        builder.setTitle("What do you want?")
                        builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                0 -> {
                                    deletesendmessage(position, holder)
                                }
                            }
                        })
                        builder.create()
                        builder.show()
                    }
                }
            }
            if (position == chatlist.size - 1 ) {
                if (chat.seen) {
                    holder.textseen!!.text = "Seen"
                    if (chat.imageurl==null) {
                        val lp: RelativeLayout.LayoutParams? =
                            holder.textseen!!.layoutParams as RelativeLayout.LayoutParams
                        lp!!.setMargins(0, 245, 10, 0)
                        holder.textseen!!.layoutParams = lp
                    }
                } else {
                    holder.textseen!!.text = "Sent"
                    if (chat.imageurl==null) {
                        val lp: RelativeLayout.LayoutParams? =
                            holder.textseen!!.layoutParams as RelativeLayout.LayoutParams
                        lp!!.setMargins(0, 245, 10, 0)
                        holder.textseen!!.layoutParams = lp
                    }
                }
            } else {
                holder.textseen!!.visibility = View.GONE
            }
        } catch (e:Exception){
            Toast.makeText(context,e.message.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(chatlist[position].senderid.equals(msenderuid)){
            1
        }
        else{
            0
        }
    }
    private fun deletesendmessage(position:Int,holder:ChatsAdapter.ChatViewHolder){
        val ref=Utils.getRef(senderUid,receiverUid)
            FirebaseFirestore.getInstance().collection(Keys.CHATS)
                .document(ref).collection(Keys.MESSAGES).document(chatlist.get(position).chatid!!)
                .update(Keys.DELETED,true)
                .addOnSuccessListener {
                Toast.makeText(context,"Message deleted",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context,it.message.toString(),Toast.LENGTH_LONG).show()
            }
    }
}