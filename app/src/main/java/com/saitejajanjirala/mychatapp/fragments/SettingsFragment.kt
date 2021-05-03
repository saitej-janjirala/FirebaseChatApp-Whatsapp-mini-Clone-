package com.saitejajanjirala.mychatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.activities.ViewfullimageActivity
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Keys
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {
    private lateinit var uid: String
    private val requestcode = 1378
    private var imageuri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverchecker: String? = null
    private var socialchecker: String? = null
    private var userup: User? = null

    private lateinit var username: TextView
    private lateinit var coverimage: ImageView
    private lateinit var profileimage: CircleImageView
    private lateinit var setfacebook:CircleImageView
    private lateinit var setinstagram:CircleImageView
    private lateinit var setEmail:CircleImageView
    private lateinit var userNumber:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        uid=FirebaseAuth.getInstance().currentUser.uid
        storageRef=FirebaseStorage.getInstance().reference
        coverimage=view.findViewById(R.id.cover_image_settings)
        profileimage=view.findViewById(R.id.profile_image_settings)
        username=view.findViewById(R.id.user_name_settings)
        setfacebook=view.findViewById(R.id.set_facebook)
        setinstagram=view.findViewById(R.id.set_instagram)
        setEmail=view.findViewById(R.id.set_email)
        userNumber=view.findViewById(R.id.user_number)
        getData()
        profileimage.setOnClickListener {
            userup?.let {
                val options = arrayOf<CharSequence>(
                    "view full image", "set another image", "delete image", "cancel"
                )
                val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                dialog.setTitle("Choose one of them")
                dialog.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->
                    when (i) {
                        0 -> {
                            if(it.picurl!=null) {
                                val intent =
                                    Intent(requireContext(), ViewfullimageActivity::class.java)
                                intent.putExtra("url", "${userup?.picurl}")
                                requireContext().startActivity(intent)
                            }
                        }
                        1 -> {
                            coverchecker = Keys.PIC_URL
                            if (checkpermission()) {
                                pickimage()
                            } else {
                                requestpermission()
                            }
                        }
                        2->{
                            progress_layout.visibility=View.VISIBLE
                            val db=FirebaseFirestore.getInstance()
                            db.collection(Keys.USERS).document(uid)
                                .update(Keys.PIC_URL,null)
                                .addOnSuccessListener {
                                    progress_layout.visibility=View.GONE
                                    Toast.makeText(requireContext(),"Successfully deleted",Toast.LENGTH_LONG).show()
                                }.addOnFailureListener {
                                    progress_layout.visibility=View.GONE
                                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                })
                dialog.create()
                dialog.show()
            }

        }

        coverimage.setOnClickListener {
            userup?.let {

                val options = arrayOf<CharSequence>(
                    "view full image", "set another image","delete image", "cancel"
                )
                val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                dialog.setTitle("Choose one of them")
                dialog.setItems(options, DialogInterface.OnClickListener { dialogInterface, i ->
                    when (i) {
                        0 -> {
                            if(it.coverurl!=null) {
                                val intent =
                                    Intent(requireContext(), ViewfullimageActivity::class.java)
                                intent.putExtra("url", "${userup?.coverurl}")
                                requireContext().startActivity(intent)
                            }
                        }
                        1 -> {
                            coverchecker = Keys.COVER_URL
                            if (checkpermission()) {
                                pickimage()
                            } else {
                                requestpermission()
                            }
                        }
                        2->{
                            progress_layout.visibility=View.VISIBLE
                            val db=FirebaseFirestore.getInstance()
                            db.collection(Keys.USERS).document(uid)
                                .update(Keys.COVER_URL,null)
                                .addOnSuccessListener {
                                    progress_layout.visibility=View.GONE
                                    Toast.makeText(requireContext(),"Successfully deleted",Toast.LENGTH_LONG).show()
                                }.addOnFailureListener {
                                    progress_layout.visibility=View.GONE
                                    Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                })
                dialog.create()
                dialog.show()
            }

        }
        setfacebook.setOnClickListener {
            socialchecker = Keys.FACEBOOK
            setsociallinks()
        }
        setinstagram.setOnClickListener {
            socialchecker = Keys.INSTAGRAM
            setsociallinks()
        }
        setEmail.setOnClickListener {
            socialchecker = Keys.EMAIL
            setsociallinks()
        }
        return view
    }
    fun getData(){
        FirebaseFirestore.getInstance().collection(Keys.USERS).document(uid)
            .addSnapshotListener(object:EventListener<DocumentSnapshot>{
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if(error==null){
                        value?.let {
                            userup=it.toObject(User::class.java)
                            Log.i("userdata","$userup")
                            userup?.let { user ->
                                username.text=user.username
                                userNumber.text="${user.phonenumber?.substring(3)}"
                                Glide.with(requireContext())
                                    .load(user.picurl)
                                    .error(R.drawable.ic_person)
                                    .into(profileimage)
                                Glide.with(requireContext())
                                    .load(user.coverurl)
                                    .error(R.drawable.cover_image)
                                    .into(coverimage)
                            }
                        }
                    }
                }

            })
    }

    fun setsociallinks() {
        try {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)
            val editext = EditText(requireContext())
            when (socialchecker) {
                Keys.FACEBOOK -> {
                    builder.setTitle(Keys.FACEBOOK)
                    editext.hint = "Enter your facebook url"
                    userup?.let {user->
                        user.facebook?.let {
                            editext.setText(Editable.Factory.getInstance().newEditable(it))
                        }
                    }
                }
                Keys.INSTAGRAM -> {
                    builder.setTitle(Keys.INSTAGRAM)
                    userup?.let {user->
                        user.instagram?.let {
                            editext.setText(Editable.Factory.getInstance().newEditable(it))
                        }
                    }
                    editext.hint = "Enter instagram url"
                }
                Keys.EMAIL-> {
                    builder.setTitle(Keys.EMAIL)
                    userup?.let {user->
                        user.email?.let {
                            editext.setText(Editable.Factory.getInstance().newEditable(it))
                        }
                    }
                    editext.hint = "Enter your email"
                }
            }
            builder.setView(editext)
            builder.setPositiveButton(
                "Add",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val str = editext.text.toString()
                    if (str == ""||str.isEmpty()||str.isBlank()) {
                        Toast.makeText(
                            context,
                            "$socialchecker should not be empty",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        saveSocialDetails(str)
                    }
                })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            })
            builder.create()
            builder.show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_LONG).show()
        }

    }

    fun saveSocialDetails(str: String) {
        val dialog = ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setTitle("Updating")
        dialog.show()
        val ref = FirebaseFirestore.getInstance().collection(Keys.USERS).document(uid)
        socialchecker?.let {
            ref.update(it,str)
                .addOnSuccessListener {
                    Toast.makeText(context, "$socialchecker is updated", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
        }
    }

    fun checkpermission(): Boolean {
        var bool: Boolean? = null
        bool = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return bool
    }

    fun requestpermission() {
        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1220)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1220 && permissions[0] == android.Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickimage()
        }
    }

    fun pickimage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, requestcode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestcode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageuri = data.data
            uploadimage(imageuri!!)
        }
    }

    fun uploadimage(uri: Uri) {
        val dialog = ProgressDialog(context)
        dialog.setMessage("image is uploading,please wait...")
        dialog.setCancelable(false)
        dialog.show()
        val fileRef = storageRef!!.child("${Keys.USER_IMAGES}/${System.currentTimeMillis()}")
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseFirestore.getInstance().collection(Keys.USERS).document(uid)
                    .update(coverchecker!!,uri.toString())
                    .addOnSuccessListener {
                        Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }
}
