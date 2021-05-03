package com.saitejajanjirala.mychatapp.fragments


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceId
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.UserAdapter
import com.saitejajanjirala.mychatapp.model.Chat
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {
    private lateinit var recyclerview: RecyclerView
    private var uid: String?=null
    private lateinit var musers: ArrayList<User>
    private lateinit var userschatlist: ArrayList<Chat>
    private lateinit var useradapter: UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats_frgment, container, false)
        uid = FirebaseAuth.getInstance().currentUser?.uid
        recyclerview = view.findViewById(R.id.recyclerviewchatlist)
        musers = ArrayList()
        useradapter = UserAdapter(requireContext(), musers, true)
        recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerview.addItemDecoration(
            DividerItemDecoration(
                recyclerview.context,
                layoutManager.orientation
            )
        )
        recyclerview.layoutManager = layoutManager
        recyclerview.adapter = useradapter
        updatetoken(FirebaseInstanceId.getInstance().token)
        return view
    }

    fun updatetoken(token: String?) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = uid?.let { FirebaseFirestore.getInstance().collection(Keys.USERS).document(it) }
        ref?.update(Keys.FCM_TOKEN, token)
    }

    override fun onResume() {
        super.onResume()
        retrievechatlist()
    }
    fun retrievechatlist() {
        FirebaseFirestore.getInstance().collection(Keys.CHATS)
            .whereEqualTo(uid!!,"yes")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error == null) {
                        value?.let {
                            val size = it.documents.size
                            musers.clear()
                            it.documents.forEach {
                                val idsList = it[Keys.IDS] as List<String>
                                idsList.forEach { id ->
                                    if (id != uid) {

                                        Log.i("ids", "$id")
                                        FirebaseFirestore.getInstance().collection(Keys.USERS)
                                            .document(id)
                                            .addSnapshotListener(object :
                                                EventListener<DocumentSnapshot> {
                                                override fun onEvent(
                                                    docvalue: DocumentSnapshot?,
                                                    error: FirebaseFirestoreException?
                                                ) {
                                                    if (error == null) {
                                                        docvalue?.let { doc ->
                                                            val user =
                                                                doc.toObject(User::class.java)
                                                            if (user != null) {
                                                                Log.i("user", "$user")
                                                                addUsers(size, user)
                                                            }
                                                        }
                                                    }
                                                }
                                            })
                                    }
                                }
                            }
                        }
                    }
                }

            })
    }


    fun addUsers(size: Int, user: User) {
        var set=0;
        for(i in 0 until musers.size){
            if(musers[i].uid==user.uid){
                musers.set(i,user)
                set=1;
            }
        }
        if(set==0) musers.add(user)
        useradapter.notifyDataSetChanged()

    }

}
