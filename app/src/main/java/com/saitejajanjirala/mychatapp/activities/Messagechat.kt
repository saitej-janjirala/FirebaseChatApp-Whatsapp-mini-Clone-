package com.saitejajanjirala.mychatapp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.ChatsAdapter
import com.saitejajanjirala.mychatapp.fragments.APIService
import com.saitejajanjirala.mychatapp.models.Chat
import com.saitejajanjirala.mychatapp.models.Users
import com.saitejajanjirala.mychatapp.notifications.*
import com.saitejajanjirala.mychatapp.utils.Connectivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Messagechat : AppCompatActivity() {
    private lateinit var attachimage: ImageView
    private lateinit var sendmessage:ImageView
    private lateinit var textmessage: EditText
    private lateinit var profileimage:CircleImageView
    private lateinit var username:TextView
    private lateinit var recyclerview:RecyclerView
    private var visituid:String?=null
    private var useruid:String?=null
    private lateinit var toolbar:Toolbar
    private lateinit var ref:DatabaseReference
    private lateinit var ref2:DatabaseReference
    lateinit var arraylist:ArrayList<Chat>
    var notify=false
    var apiservice:APIService?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagechat)
        Log.i("oncreate","oncreate")
        visituid=intent.getStringExtra("uid")
        useruid=getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid","")
        toolbar=findViewById(R.id.toolbar_mainchat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        attachimage=findViewById(R.id.attachimage)
        sendmessage=findViewById(R.id.sendmessage)
        textmessage=findViewById(R.id.text_message)
        profileimage=findViewById(R.id.profile_imagechat)
        username=findViewById(R.id.usernamechat)
        recyclerview=findViewById(R.id.chatrecyclerview)
        apiservice=Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        ref2=FirebaseDatabase.getInstance().reference.child("users").child(visituid!!)
        ref2.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val user=p0.getValue(Users::class.java)
                username.text=user!!.getusername()
                Picasso.get().load(user.getprofileurl()).error(R.drawable.ic_person).into(profileimage)
                    retrievemessages(useruid, visituid!!, user.getprofileurl())
            }
        })
        attachimage.setOnClickListener {
            val obj=Connectivity(this)
            if(obj.checkconnectivity()) {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "pick image"), 1378)
            }
            else{
                obj.showdialog()
            }
        }
        sendmessage.setOnClickListener {

            if(textmessage.text.isNullOrBlank()){
                Toast.makeText(this@Messagechat,"Message shouldn't be empty",Toast.LENGTH_LONG).show()
            }
            else{
                val message=textmessage.text.toString()
                if(visituid==null){
                    Toast.makeText(this@Messagechat,"Unable to find user",Toast.LENGTH_LONG).show()
                }
                else{
                    val obj= Connectivity(this@Messagechat)
                    if(obj.checkconnectivity()){
                        sendmessagetouser(useruid!!,visituid!!,message)
                    }
                    else{
                        obj.showdialog()
                    }
                }
            }
            textmessage.text=null
        }
        toolbar.setOnClickListener {
            val intent=Intent(this,VisituserprofileActivity::class.java)
            intent.putExtra("uid",visituid)
            startActivity(intent)
        }
    }
    private fun retrievemessages(msenderid: String?,mrecieverid: String, mprofileurl: String?) {
        arraylist= ArrayList()
        val chatsref=FirebaseDatabase.getInstance().reference.child("chats")
        chatsref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                arraylist.clear()
                p0.children.forEach {
                    val chats=it.getValue(Chat::class.java)
                    if((chats!!.getreciever().equals(msenderid) && chats.getsender().equals(mrecieverid))||
                        (chats.getreciever().equals(mrecieverid) && chats.getsender().equals(msenderid))) {
                        arraylist.add(chats)
                    }
                }
                val adapter=ChatsAdapter(this@Messagechat,arraylist,mprofileurl!!)
                val layoutmanager=LinearLayoutManager(this@Messagechat)
                layoutmanager.stackFromEnd=true
                recyclerview.layoutManager=layoutmanager
                recyclerview.adapter=adapter
            }
        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home->{
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1378 && resultCode==Activity.RESULT_OK && data!!.data!=null){
            val dialog=ProgressDialog(this@Messagechat)
            dialog.setTitle("Image is uploading please wait")
            dialog.setCancelable(false)
            dialog.show()
            val uri:Uri=data.data!!
            val storageref=FirebaseStorage.getInstance().reference.child("chatmages")
            val dbref=FirebaseDatabase.getInstance().reference
            val messageid=dbref.push().key
            val filepath=storageref.child("$messageid.jpg")
            filepath.putFile(uri).addOnSuccessListener {
                filepath.downloadUrl.addOnSuccessListener {
                    val url=it.toString()!!
                    val map=HashMap<String,Any?>()
                    map["sender"]=useruid
                    map["message"]="sent you an image."
                    map["reciever"]=visituid
                    map["isseen"]=false
                    map["url"]=url
                    map["messageid"]=messageid
                     dbref.child("chats")
                         .child(messageid!!)
                         .setValue(map)
                         .addOnSuccessListener {
                             notify=true
                             val chatslistref=FirebaseDatabase.getInstance().reference.child("chatlist")
                                 .child(useruid!!)
                                 .child(visituid!!)
                              chatslistref.addListenerForSingleValueEvent(object:ValueEventListener{
                                  override fun onCancelled(p0: DatabaseError) {
                                  }
                                  override fun onDataChange(p0: DataSnapshot) {
                                      if(!p0.exists()) {
                                          chatslistref.child("id").setValue(visituid)
                                      }
                                     val chatlistreciver =FirebaseDatabase.getInstance().reference.child("chatlist")
                                                                           .child(visituid!!).child(useruid!!)
                                     chatlistreciver.child("id").setValue(useruid)
                                      dialog.dismiss()
                                  }
                              })
                             //implementing push notifications
                             try {
                                implementpush(visituid!!,"sent you an image.")
                             }
                             catch(e:Exception){
                                 Log.i("exception",e.message.toString())
                                 Toast.makeText(this@Messagechat,e.message.toString(),Toast.LENGTH_LONG).show()
                             }

                         }
                         .addOnFailureListener {
                             Toast.makeText(this@Messagechat,it.message.toString(),Toast.LENGTH_LONG).show()
                             dialog.dismiss()
                         }

                }
            }.addOnFailureListener {
                          Toast.makeText(this@Messagechat,it.message.toString(),Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                }

        }
    }
    fun sendmessagetouser(senderuid:String,visiteduid:String,msg:String){
        ref=FirebaseDatabase.getInstance().reference
        val messagekey=ref.push().key
        val messagemap=HashMap<String,Any?>()
        messagemap["sender"]=senderuid
        messagemap["message"]=msg
        messagemap["reciever"]=visiteduid
        messagemap["isseen"]=false
        messagemap["url"]=""
        messagemap["messageid"]=messagekey
        ref.child("chats")
            .child(messagekey!!)
            .setValue(messagemap)
            .addOnSuccessListener {
                notify=true
                val chatslistref=FirebaseDatabase.getInstance().reference.child("chatlist")
                    .child(senderuid)
                    .child(visiteduid)
                 chatslistref.addListenerForSingleValueEvent(object:ValueEventListener{
                     override fun onCancelled(p0: DatabaseError) {
                     }
                     override fun onDataChange(p0: DataSnapshot) {
                         if(!p0.exists()) {
                             chatslistref.child("id").setValue(visiteduid)
                         }
                        val chatlistreciver =FirebaseDatabase.getInstance().reference.child("chatlist")
                                                              .child(visiteduid).child(senderuid)
                        chatlistreciver.child("id").setValue(senderuid)
                     }
                 })
                //implementing push notifications
                try {
                    implementpush(visiteduid,msg)

                }
                catch(e:Exception){
                    Log.i("exception",e.message.toString())
                    Toast.makeText(this@Messagechat,e.message.toString(),Toast.LENGTH_LONG).show()
                }

            }
            .addOnFailureListener {
                  Toast.makeText(this@Messagechat,it.message.toString(),Toast.LENGTH_LONG).show()
            }
    }
    var seenlistener:ValueEventListener?=null
    override fun onResume() {
        super.onResume()
        seenmessage(visituid!!)
    }
    private fun seenmessage(xuid:String){
        val xref=FirebaseDatabase.getInstance().reference.child("chats")
        seenlistener=xref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val chat=it.getValue(Chat::class.java)
                    if(chat!!.getreciever().equals(useruid) && chat.getsender().equals(xuid)){
                        val map=HashMap<String,Any>()
                        map["isseen"]=true
                        it.ref.updateChildren(map)
                    }
                }
            }

        })
    }

    fun implementpush(yuid:String,ymsg:String){
        val reference1 =
            FirebaseDatabase.getInstance().reference.child("users").child(useruid!!)
        reference1.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(Users::class.java)!!
                if (notify) {
                    sendnotification(yuid, user.getusername(), ymsg)
                }

            }
        })
    }
    override fun onPause() {
        super.onPause()
        ref2.removeEventListener(seenlistener!!)
    }
    private fun sendnotification(receiverid:String?,username:String?,message:String){
        val ref=FirebaseDatabase.getInstance().reference.child("Tokens")
        val query=ref.orderByKey().equalTo(receiverid)
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val token:Token?=it.getValue(Token::class.java)
                    val data=Data(useruid,R.mipmap.ic_launcher,"$username: $message","New Message",receiverid)
                    val sender=Sender(data!!,token!!.gettoken().toString())
                    apiservice!!.sendNotification(sender)
                        .enqueue(object: Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            }
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code()==200){
                                    if(response.body()!!.success!==1){
                                        Toast.makeText(this@Messagechat,"failed nothing happend",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                        })
                }
            }
        })
    }

}
