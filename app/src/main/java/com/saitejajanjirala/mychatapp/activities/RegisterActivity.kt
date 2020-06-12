package com.saitejajanjirala.mychatapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import android.os.Handler
import android.text.TextUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.utils.EmailValidator

class RegisterActivity : AppCompatActivity() {
    lateinit var namelayout: TextInputLayout
    lateinit var nametext: TextInputEditText
    lateinit var emaillayout:TextInputLayout
    lateinit var signupemail:TextInputEditText
    lateinit var passwordlayout: TextInputLayout
    lateinit var signuppassword:TextInputEditText
    lateinit var confirmpasswordlayout:TextInputLayout
    lateinit var signupconfirmpassword:TextInputEditText
    lateinit var Register: Button
    lateinit var mauth:FirebaseAuth
    lateinit var toprelative:RelativeLayout
    lateinit var refusers:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        namelayout=findViewById(R.id.namelayout)
        nametext=findViewById(R.id.nametext)
        emaillayout=findViewById(R.id.emaillayout)
        signupemail=findViewById(R.id.signupemail)
        passwordlayout=findViewById(R.id.passwordlayout)
        signuppassword=findViewById(R.id.signuppassword)
        confirmpasswordlayout=findViewById(R.id.confirmpasswordlayout)
        signupconfirmpassword=findViewById(R.id.signupconfirmpassword)
        Register=findViewById(R.id.register)
        toprelative=findViewById(R.id.toprelative)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        nametext.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.trim().length<3){
                    namelayout.error="Name should be atleast 3 letters"
                }
                else{
                    namelayout.isErrorEnabled=false
                }
            }
        })
        signupemail.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!EmailValidator().isEmailValid(signupemail.text.toString()
                    )){
                    emaillayout.error="Enter valid email"
                }
                else{
                    emaillayout.isErrorEnabled=false
                }
            }
        })
        signuppassword.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.trim().length<6 || signuppassword.text.isNullOrBlank()){
                    passwordlayout.error="password shouldn't be less than 6 characters"
                }
                else{
                    passwordlayout.isErrorEnabled=false
                }
            }
        })
        signupconfirmpassword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.trim().length<6 || signupconfirmpassword.text.isNullOrBlank()){
                    confirmpasswordlayout.error="password shouldn't be less than 6 characters"
                }
                else if(signupconfirmpassword.text.toString()!=signuppassword.text.toString()){
                    confirmpasswordlayout.error="both passwords should be same"
                }
                else {
                    confirmpasswordlayout.isErrorEnabled = false
                }
            }
        })
        Register.setOnClickListener {
            val view=currentFocus
            val imm=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (view != null) {
                imm.hideSoftInputFromWindow(view.windowToken,0)
            }
            if(confirmpasswordlayout.isErrorEnabled||passwordlayout.isErrorEnabled||emaillayout.isErrorEnabled||namelayout.isErrorEnabled){
                Snackbar.make(toprelative,"Enter details properly",Snackbar.LENGTH_LONG)
                    .setAction("Close") { }
                    .setActionTextColor(resources.getColor(R.color.colorAccent))
                    .show()
            }
            else{
                val pdialog=ProgressDialog(this@RegisterActivity)
                pdialog.setTitle("Please Wait")
                pdialog.create()
                pdialog.setCancelable(false)
                pdialog.show()
                mauth= FirebaseAuth.getInstance()
                mauth.createUserWithEmailAndPassword(signupemail.text.toString(),signuppassword.text.toString())
                        .addOnSuccessListener {
                        val uid= mauth.currentUser!!.uid
                        refusers=FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        val usersHashMap=HashMap<String,Any>()
                        usersHashMap["uid"]=uid
                        usersHashMap["username"]=nametext.text.toString()
                        usersHashMap["profileurl"]="https://firebasestorage.googleapis.com/v0/b/mychatapp-c6209.appspot.com/o/undraw_profile_pic_ic5t.png?alt=media&token=ef9608a4-cee9-46e4-955d-1520c713b255"
                        usersHashMap["coverurl"]="https://firebasestorage.googleapis.com/v0/b/mychatapp-c6209.appspot.com/o/wp1700121-cool-naruto-desktop-wallpapers.png?alt=media&token=778b8070-e178-4b27-95b1-a4171ac3235a"
                        usersHashMap["status"]="offline"
                        usersHashMap["search"]=nametext.text.toString().toLowerCase()
                        usersHashMap["facebook"]="https://www.facebook.com"
                        usersHashMap["instagram"]="https://www.instagram.com"
                        usersHashMap["website"]="https://www.google.com"
                        refusers.updateChildren(usersHashMap)
                            .addOnSuccessListener {
                                pdialog.dismiss()
                                Toast.makeText(this@RegisterActivity,"Successfully Registered Go back and Login",Toast.LENGTH_LONG).show()
                                Handler().postDelayed({
                                    startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                                },800)
                            }
                            .addOnFailureListener {
                                pdialog.dismiss()
                                Toast.makeText(this@RegisterActivity,it.message.toString(),Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener {
                        pdialog.dismiss()
                        Toast.makeText(this@RegisterActivity,it.message.toString(),Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                super.onBackPressed()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
