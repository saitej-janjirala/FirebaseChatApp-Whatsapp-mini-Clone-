package com.saitejajanjirala.mychatapp.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.UserAdapter
import com.saitejajanjirala.mychatapp.models.Users

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    lateinit var adapter:UserAdapter
    lateinit var arraylist:ArrayList<Users>
    lateinit var uid:String
    lateinit var searchtext:EditText
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_search, container,false)
        arraylist= ArrayList()
        searchtext=view.findViewById(R.id.searchtext)
        recyclerView=view.findViewById(R.id.searchlist)
        uid= requireContext().getSharedPreferences("user",Context.MODE_PRIVATE).getString("uid","").toString()
        retrieve()
        val layoutManager=LinearLayoutManager(context)
        layoutManager.orientation=LinearLayoutManager.VERTICAL
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context,layoutManager.orientation))
        recyclerView.layoutManager=layoutManager
        searchtext.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchforusers(p0.toString().toLowerCase())
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        return view

    }
    private fun retrieve(){
        val refusers=FirebaseDatabase.getInstance().reference.child("users")
        refusers.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(searchtext.text.toString().equals("")) {
                    arraylist.clear()
                    p0.children.forEach { ittop ->
                        val users: Users? = ittop.getValue(Users::class.java)
                        if (!(users!!.getuid()).equals(uid)) {
                            arraylist.add(users)
                        }
                    }
                    adapter = UserAdapter(context!!, arraylist, false)
                    recyclerView.adapter = adapter
                }

            }

        })
    }
    private fun searchforusers(str:String){
        val queryusers=FirebaseDatabase.getInstance().reference.child("users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")
            queryusers.addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    arraylist.clear()
                    p0.children.forEach { ittop ->
                        val users:Users?=ittop.getValue(Users::class.java)
                        if(!users!!.getuid().equals(uid)){
                            arraylist.add(users)
                        }
                    }
                    adapter= UserAdapter(context!!,arraylist,false)
                    recyclerView.adapter=adapter
            }

            })
    }

}
