package com.saitejajanjirala.mychatapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.activities.ViewfullimageActivity
import com.saitejajanjirala.mychatapp.models.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.jar.Manifest

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {
    private lateinit var coverimage:ImageView
    private lateinit var profileimage:CircleImageView
    private lateinit var uid:String
    private lateinit var username:TextView
    private lateinit var mref:DatabaseReference
    private val requestcode=1378
    private var imageuri:Uri?=null
    private var storageref:StorageReference?=null
    private var coverchecker:String?=null
    private lateinit var setfacebook:CircleImageView
    private lateinit var setinstagram:CircleImageView
    private lateinit var setwebsite:CircleImageView
    private var socialchecker:String?=null
    private var userup:Users?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        coverimage=view.findViewById(R.id.coverimagesettings)
        profileimage=view.findViewById(R.id.profileimagesettings)
        username=view.findViewById(R.id.usernamesettings)
        setfacebook=view.findViewById(R.id.set_facebook)
        setinstagram=view.findViewById(R.id.set_instagram)
        setwebsite=view.findViewById(R.id.set_webiste)
        uid= requireContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("uid","").toString()
        storageref=FirebaseStorage.getInstance().reference.child("userimages")
        mref=FirebaseDatabase.getInstance().reference.child("users").child(uid)
        mref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    userup= p0.getValue(Users::class.java)
                    Picasso.get().load(userup!!.getcoverurl()).error(R.color.grey).into(coverimage)
                    Picasso.get().load(userup!!.getprofileurl()).error(R.drawable.ic_person)
                        .into(profileimage)
                    username.text=userup!!.getusername()
                }

            }
        })
        profileimage.setOnClickListener{
            val options= arrayOf<CharSequence>(
                "view full image","set another image","cancel"
            )
            val dialog=androidx.appcompat.app.AlertDialog.Builder(requireContext())
            dialog.setTitle("Choose one of them")
            dialog.setItems(options,DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    0->{
                        val intent = Intent(requireContext(), ViewfullimageActivity::class.java)
                        intent.putExtra("url",userup!!.getcoverurl())
                        requireContext().startActivity(intent)
                    }
                    1->{
                        coverchecker = "profile"
                        if (checkpermission()) {
                            pickimage()
                        } else {
                            requestpermission()
                        }
                    }
                }
            })
            dialog.create()
            dialog.show()
        }
        coverimage.setOnClickListener {
            val options= arrayOf<CharSequence>(
                "view full image","set another image","cancel"
            )
            val dialog=androidx.appcompat.app.AlertDialog.Builder(requireContext())
            dialog.setTitle("Choose one of them")
            dialog.setItems(options,DialogInterface.OnClickListener { dialogInterface, i ->
                when(i){
                    0->{
                        val intent = Intent(requireContext(), ViewfullimageActivity::class.java)
                        intent.putExtra("url",userup!!.getprofileurl() )
                        requireContext().startActivity(intent)
                    }
                    1->{
                        coverchecker = "cover"
                        if (checkpermission()) {
                            pickimage()
                        } else {
                            requestpermission()
                        }
                    }
                }
            })
            dialog.create()
            dialog.show()
        }
        username.setOnClickListener {
            socialchecker="username"
            setsociallinks()
        }
        setfacebook.setOnClickListener {
            socialchecker="facebook"
            setsociallinks()
        }
        setinstagram.setOnClickListener {
            socialchecker="instagram"
            setsociallinks()
        }
        setwebsite.setOnClickListener {
            socialchecker="website"
            setsociallinks()
        }
        return view
    }
    fun setsociallinks(){
        try {
            val builder: AlertDialog.Builder =
                AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert)
            val editext = EditText(requireContext())
            when (socialchecker) {
                "facebook" -> {
                    builder.setTitle("facebook")
                    editext.hint = "Enter your facebook url"
                }
                "instagram" -> {
                    builder.setTitle("instagram")
                    editext.hint = "Enter instagram url"
                }
                "website" -> {
                    builder.setTitle("website")
                    editext.hint = "Enter your website url"
                }
                else -> {
                    builder.setTitle("Username")
                    editext.hint = "enter your username"
                }
            }
            builder.setView(editext)
            builder.setPositiveButton(
                "Create",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val str = editext.text.toString()
                    if (str == "") {
                        Toast.makeText(
                            context,
                            "$socialchecker should not be empty",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        savescoialdetails(str)
                    }
                })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            })
            builder.create()
            builder.show()
        }
        catch(e:Exception){
            Toast.makeText(requireContext(),e.message.toString(),Toast.LENGTH_LONG).show()
        }

    }
    fun savescoialdetails(str:String){
        val dialog=ProgressDialog(requireContext())
        dialog.setCancelable(false)
        dialog.setTitle("Updating")
        dialog.show()
        val ref=FirebaseDatabase.getInstance().reference.child("users").child(uid)
        val map=HashMap<String,Any>()
        map[socialchecker!!]=str
        ref.updateChildren(map)
            .addOnSuccessListener {
                Toast.makeText(context,"$socialchecker is updated",Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(context,it.message.toString(),Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
    }
    fun checkpermission():Boolean{
        var bool:Boolean?=null
        bool = ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        return bool
    }
    fun requestpermission(){
        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1220)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1220 && permissions[0]==android.Manifest.permission.READ_EXTERNAL_STORAGE && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            pickimage()
        }
    }
    fun pickimage(){
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,1378)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==requestcode && resultCode==Activity.RESULT_OK && data!!.data!=null){
            imageuri=data.data
            uploadimage(imageuri!!)
        }
    }
    fun uploadimage(uri:Uri){
        val dialog=ProgressDialog(context)
        dialog.setMessage("image is uploading,please wait...")
        dialog.setCancelable(false)
        dialog.show()
        val fileref=storageref!!.child("${System.currentTimeMillis()}.jpg")
        fileref.putFile(uri).addOnSuccessListener {
                            fileref.downloadUrl.addOnSuccessListener {uri->
                                val ref=FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                val url=uri.toString()
                                if(coverchecker=="cover"){
                                    val map=HashMap<String,Any>()
                                    map["coverurl"]=url
                                    ref.updateChildren(map)
                                    coverchecker=""
                                }
                                else if(coverchecker=="profile"){
                                    val map=HashMap<String,Any>()
                                    map["profileurl"]=url
                                    ref.updateChildren(map)
                                    coverchecker=""
                                }
                                Toast.makeText(context,"Successfully Uploaded",Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
        }.addOnFailureListener {
                Toast.makeText(context,it.message.toString(),Toast.LENGTH_LONG).show()
                dialog.dismiss()
        }
    }
}
