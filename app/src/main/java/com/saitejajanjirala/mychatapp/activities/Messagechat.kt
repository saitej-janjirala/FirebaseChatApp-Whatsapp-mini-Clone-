package com.saitejajanjirala.mychatapp.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.ChatsAdapter
import com.saitejajanjirala.mychatapp.fragments.APIService
import com.saitejajanjirala.mychatapp.model.Chat
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.models.Users
import com.saitejajanjirala.mychatapp.notifications.*
import com.saitejajanjirala.mychatapp.utils.Connectivity
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import com.saitejajanjirala.mychatapp.utils.Utils.getRef
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_messagechat.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Messagechat : AppCompatActivity() {
    private lateinit var attachimage: ImageView
    private lateinit var sendmessage: ImageView
    private lateinit var textmessage: EditText
    private lateinit var profileimage: CircleImageView
    private lateinit var username: TextView
    private lateinit var recyclerview: RecyclerView
    private var visituid: String? = null
    private var useruid: String? = null
    private lateinit var toolbar: Toolbar
    private lateinit var ref: DatabaseReference
    private lateinit var ref2: DatabaseReference
    lateinit var arraylist: ArrayList<Chat>
    var notify = false
    lateinit var receiverUser: User
    var currentUser:User?=null
    var apiservice: APIService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messagechat)
        visituid = intent.getStringExtra("uid")
        if (visituid == null) {
            finish()
        }
        useruid = FirebaseAuth.getInstance().currentUser.uid
        toolbar = findViewById(R.id.toolbar_mainchat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        attachimage = findViewById(R.id.attachimage)
        sendmessage = findViewById(R.id.sendmessage)
        textmessage = findViewById(R.id.text_message)
        profileimage = findViewById(R.id.profile_imagechat)
        username = findViewById(R.id.usernamechat)
        recyclerview = findViewById(R.id.chatrecyclerview)
        apiservice =
            Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)
        visituid?.let {
            FirebaseFirestore.getInstance().collection(Keys.USERS)
                .document(it)
                .addSnapshotListener(object : EventListener<DocumentSnapshot> {
                    override fun onEvent(
                        value: DocumentSnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error == null) {
                            value?.let { doc ->
                                val user = doc.toObject(User::class.java)
                                user?.let { mUser ->
                                    if (!this@Messagechat.isDestroyed) {
                                        username.text = mUser.username
                                        Glide.with(this@Messagechat)
                                            .load(mUser.picurl)
                                            .error(R.drawable.ic_person)
                                            .into(profileimage)
                                        receiverUser = mUser
                                        retrievemessages(useruid, visituid!!, mUser.picurl)
                                    }
                                }
                            }
                        }
                    }

                })
        }
        useruid?.let {
            FirebaseFirestore.getInstance().collection(Keys.USERS)
                .document(it)
                .addSnapshotListener(object : EventListener<DocumentSnapshot> {
                    override fun onEvent(
                        value: DocumentSnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if (error == null) {
                            value?.let { doc ->
                                val user = doc.toObject(User::class.java)
                                user?.let { mUser ->
                                    currentUser=mUser
                                }
                            }
                        }
                    }
                })
        }

        attachimage.setOnClickListener {
            val obj = Connectivity(this)
            if (obj.checkConnectivity()) {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "pick image"), 1378)
            } else {
                obj.showDialog()
            }
        }
        sendmessage.setOnClickListener {
            if (textmessage.text.isNullOrBlank()) {
                Toast.makeText(this@Messagechat, "Message shouldn't be empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                val message = textmessage.text.toString()
                if (visituid == null) {
                    Toast.makeText(this@Messagechat, "Unable to find user", Toast.LENGTH_LONG)
                        .show()
                    finish()
                } else {
                    val obj = Connectivity(this@Messagechat)
                    if (obj.checkConnectivity()) {
                        sendMessagetoUser(useruid!!, visituid!!, message)
                    } else {
                        obj.showDialog()
                    }
                }
            }
            textmessage.text = null
        }
        toolbar.setOnClickListener {
            val intent = Intent(this, VisituserprofileActivity::class.java)
            intent.putExtra("uid", visituid)
            startActivity(intent)
        }
    }

    private fun retrievemessages(msenderid: String?, mrecieverid: String, mprofileurl: String?) {
        arraylist = ArrayList()
        val ref = getRef(msenderid!!, mrecieverid)
        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(ref)
            .collection(Keys.MESSAGES)
            .orderBy(Keys.TIME_STAMP)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error == null) {
                        value?.let {
                            arraylist.clear()
                            it.documents.forEach {
                                val chat = it.toObject(Chat::class.java)
                                if (chat != null && !chat.deleted) {
                                    arraylist.add(chat)
                                }
                            }
                            var imageurl: String? = mprofileurl
                            if (imageurl == null) {
                                imageurl =
                                    "https://cdn1.iconfinder.com/data/icons/business-office-and-internet-3-4/48/129-512.png"
                            }
                            val adapter = ChatsAdapter(
                                this@Messagechat,
                                useruid!!,
                                visituid!!,
                                arraylist,
                                imageurl
                            )
                            val layoutmanager = LinearLayoutManager(this@Messagechat)
                            layoutmanager.stackFromEnd = true
                            recyclerview.layoutManager = layoutmanager
                            recyclerview.adapter = adapter
                        }
                    }
                }

            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1378 && resultCode == Activity.RESULT_OK && data!!.data != null) {
            val dialog = ProgressDialog(this@Messagechat)
            dialog.setTitle("Image is uploading please wait")
            dialog.setCancelable(false)
            dialog.show()
            val uri: Uri = data.data!!
            val fileRef =
                FirebaseStorage.getInstance().reference.child(Keys.CHAT_IMAGES + "/" + "${System.currentTimeMillis()}")
            fileRef.putFile(uri)
                .addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        val url = uri.toString()
                        val msg = "sent you an image"
                        sendMessagetoUser(useruid!!, visituid!!, msg, url)
                        dialog.dismiss()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this@Messagechat, it.message.toString(), Toast.LENGTH_LONG)
                        .show()
                    dialog.dismiss()
                }

        }
    }

    fun sendMessagetoUser(
        senderuid: String,
        visiteduid: String,
        msg: String,
        imageUrl: String? = null
    ) {
        val ref = getRef(senderuid, visiteduid)
        val chat =
            Chat(senderuid, visiteduid, imageUrl, msg, false, System.currentTimeMillis())
        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(getRef(senderuid, visiteduid))
            .get()
            .addOnSuccessListener {
                if (it[Keys.TIME_STAMP] == null) {
                    val chat1 = Chat(visituid, useruid, deleted = true)
                    val chat2 = Chat(useruid, visituid, deleted = true)
                    val msgRef =
                        FirebaseFirestore.getInstance().collection(Keys.CHATS).document(ref)
                            .collection(Keys.MESSAGES)
                    msgRef.add(chat1)
                    msgRef.add(chat2)
                    sendChatMessage(ref, chat, true)
                } else {
                    sendChatMessage(ref, chat, false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@Messagechat, it.message.toString(), Toast.LENGTH_LONG).show()
            }

    }

    private fun sendChatMessage(ref: String, chat: Chat, isfirst: Boolean) {

        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(ref)
            .collection(Keys.MESSAGES)
            .add(chat)
            .addOnSuccessListener {
                it.update(Keys.CHAT_ID, it.id)
                val map = HashMap<String, Any>()
                if (isfirst) {
                    map.put(useruid!!, "yes")
                    map.put(visituid!!, "yes")
                    map.put(Keys.IDS, listOf(useruid, visituid))
                    map.put(Keys.TIME_STAMP, chat.timestamp!!)
                    Log.i("first", "true")
                    FirebaseFirestore.getInstance().collection(Keys.CHATS).document(ref).set(map)

                } else {
                    Log.i("first", "false")
                    FirebaseFirestore.getInstance().collection(Keys.CHATS).document(ref)
                        .update(Keys.TIME_STAMP, chat.timestamp)
                }
                try {
                    val token = receiverUser.fcmtoken
                    val data = Data(
                        useruid,
                        R.drawable.my_logo_noti,
                        "${currentUser?.username}: ${chat.message}",
                        "New Message",
                        receiverUser.uid
                    )
                    val sender = Sender(data!!, token!!)
                    apiservice!!.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse> {
                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                            }

                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success !== 1) {
                                        Toast.makeText(
                                            this@Messagechat,
                                            "failed nothing happend",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }

                        })

                }
                catch(e:Exception){
                    Log.i("exception",e.message.toString())
                    Toast.makeText(this@Messagechat,e.message.toString(),Toast.LENGTH_LONG).show()
                }
                text_message.text = Editable.Factory.getInstance().newEditable("")
            }
    }

    var seenlistener: ListenerRegistration? = null
    override fun onResume() {
        super.onResume()
        seenmessage(visituid!!)
    }

    private fun seenmessage(xuid: String) {
        val xref = FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .document(getRef(useruid!!, xuid))
            .collection(Keys.MESSAGES)
            .whereEqualTo(Keys.RECEIVER_ID, useruid)
            .whereEqualTo(Keys.SEEN, false)

        seenlistener = xref.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error == null) {
                    value?.let {
                        if (it.documents.size > 0) {
                            it.documents.forEach { doc ->
                                FirebaseFirestore.getInstance().collection(Keys.CHATS)
                                    .document(getRef(useruid!!, xuid))
                                    .collection(Keys.MESSAGES)
                                    .document(doc.id)
                                    .update(Keys.SEEN, true)
                            }
                        }
                    }
                }
            }
        })
    }


    override fun onPause() {
        super.onPause()
        seenlistener?.remove()
    }

    private fun sendnotification(receiverid: String?, username: String?, message: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val query = ref.orderByKey().equalTo(receiverid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {

                }
            }
        })
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
