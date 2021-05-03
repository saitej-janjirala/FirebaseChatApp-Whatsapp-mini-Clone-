package com.saitejajanjirala.mychatapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.utils.Keys
import com.saitejajanjirala.mychatapp.utils.Utils
import kotlinx.android.synthetic.main.activity_entername.*

class EnternameActivity : AppCompatActivity() {
    private var userName=""
    private var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entername)
        uid=FirebaseAuth.getInstance().currentUser.uid
        user_name_text.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                userName=user_name_text.text.toString()
                if(userName!=null && userName.trim().length>=3){
                    user_name_layout.isErrorEnabled=false
                }
                else{
                    user_name_layout.error="user name must be more than 3 characters"
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }

        })
        continue_button.setOnClickListener {
            if(!user_name_layout.isErrorEnabled){

                val db=FirebaseFirestore.getInstance()
                db.collection(Keys.USERS).whereEqualTo(Keys.USER_NAME,userName)
                    .get()
                    .addOnSuccessListener {
                        if(it.documents.size>0){
                            Toast.makeText(this,"The username already exists",Toast.LENGTH_LONG).show()
                        }
                        else{
                            db.collection(Keys.USERS).document(uid)
                                .update(Keys.USER_NAME,userName)
                                .addOnSuccessListener {
                                    Toast.makeText(this,"username saved",Toast.LENGTH_LONG).show()
                                    startActivity(Intent(this@EnternameActivity,MainActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {exe->
                                    Toast.makeText(this,exe.message,Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                    }

            }
            else{
                Utils.showSnackBar(this,top_layout,"user name must be more than 3 characters")
            }
        }
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