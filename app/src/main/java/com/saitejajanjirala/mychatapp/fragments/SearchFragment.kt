package com.saitejajanjirala.mychatapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.adapters.UserAdapter
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys

/**
 * A simple [Fragment] subclass.
 */
class SearchFragment : Fragment() {
    lateinit var adapter: UserAdapter
    lateinit var arraylist: ArrayList<User>
    lateinit var uid: String
    lateinit var searchtext: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var searchButton: ImageView
    lateinit var emptyLayout: ConstraintLayout
    lateinit var progressLayout:ConstraintLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        arraylist = ArrayList()
        searchtext = view.findViewById(R.id.searchtext)
        recyclerView = view.findViewById(R.id.searchlist)
        searchButton = view.findViewById(R.id.search_icon)
        emptyLayout = view.findViewById(R.id.empty_layout)
        progressLayout=view.findViewById(R.id.progress_layout)
        uid = FirebaseAuth.getInstance().currentUser.uid
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                layoutManager.orientation
            )
        )
        recyclerView.layoutManager = layoutManager
        searchButton.setOnClickListener {
            val imm: InputMethodManager =
                requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            val str = searchtext.text.toString()
            if (str == null || str.isBlank() || str.isEmpty()) {
                Toast.makeText(context, "query should not be empty", Toast.LENGTH_LONG).show()
            } else {
                searchforusers(str)
            }
        }

        return view

    }

    //    private fun retrieve(){
//        val refusers=FirebaseDatabase.getInstance().reference.child("users")
//        refusers.addValueEventListener(object:ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//                if(searchtext.text.toString().equals("")) {
//                    arraylist.clear()
//                    p0.children.forEach { ittop ->
//                        val users: Users? = ittop.getValue(Users::class.java)
//                        if (!(users!!.getuid()).equals(uid)) {
//                            arraylist.add(users)
//                        }
//                    }
//                    adapter = UserAdapter(context!!, arraylist, false)
//                    recyclerView.adapter = adapter
//                }
//
//            }
//
//        })
//    }
    private fun searchforusers(str: String) {
        emptyLayout.visibility = View.GONE
        progressLayout.visibility=View.VISIBLE
        FirebaseFirestore.getInstance().collection(Keys.USERS)
            .whereEqualTo(Keys.USER_NAME, str)
            .get()
            .addOnSuccessListener {
                progressLayout.visibility=View.GONE
                arraylist.clear()
                it.documents.forEach {
                    val user = it.toObject(User::class.java)
                    if (user != null && user.uid!=uid) {
                        arraylist.add(user)
                    }
                }
                if (arraylist.isEmpty()) {
                    emptyLayout.visibility = View.VISIBLE
                } else {
                    adapter = UserAdapter(requireContext(), arraylist, false)
                    recyclerView.adapter = adapter
                }
            }
            .addOnFailureListener {
                progressLayout.visibility=View.GONE
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
//            queryusers.addValueEventListener(object:ValueEventListener{
//                override fun onCancelled(p0: DatabaseError) {
//                }
//                override fun onDataChange(p0: DataSnapshot) {
//
//                    p0.children.forEach { ittop ->
//
//                    }
//                    adapter= UserAdapter(context!!,arraylist,false)
//
//            }
//
//            })
    }

}
