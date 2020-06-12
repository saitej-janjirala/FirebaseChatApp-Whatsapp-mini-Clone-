package com.saitejajanjirala.mychatapp.activities
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.utils.Connectivity
import com.saitejajanjirala.mychatapp.utils.EmailValidator

class LoginActivity : AppCompatActivity() {
    lateinit var emaillayout:TextInputLayout
    lateinit var emailtext:TextInputEditText
    lateinit var passwordlayout:TextInputLayout
    lateinit var passwordtext:TextInputEditText
    lateinit var login:Button
    lateinit var toplayout:LinearLayout
    lateinit var gotoregister:TextView
    lateinit var mauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emaillayout=findViewById(R.id.loginemaillayout)
        emailtext=findViewById(R.id.loginemail)
        passwordlayout=findViewById(R.id.loginpasswordlayout)
        passwordtext=findViewById(R.id.loginpassword)
        login=findViewById(R.id.login)
        toplayout=findViewById(R.id.toplayout)
        gotoregister=findViewById(R.id.gotoregistertaion)
        gotoregister.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
        }
        emailtext.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!EmailValidator().isEmailValid(emailtext.text.toString())){
                    emaillayout.error="Enter valid email"
                }
                else{
                    emaillayout.isErrorEnabled=false
                }
            }
        })
        passwordtext.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(passwordtext.text.isNullOrEmpty()){
                    passwordlayout.error="password"
                }
                else{
                    passwordlayout.isErrorEnabled=false
                }
            }
        })
        login.setOnClickListener {
            val view=currentFocus
            if(view!=null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            if(passwordlayout.isErrorEnabled||emaillayout.isErrorEnabled){
                Snackbar.make(toplayout,"Enter details properly",Snackbar.LENGTH_LONG)
                    .setAction("Close",object:View.OnClickListener{
                        override fun onClick(p0: View?) {
                        }
                    })
                    .setActionTextColor(resources.getColor(R.color.colorAccent))
                    .show()
            }
            else{
                val obj=Connectivity(this@LoginActivity)
                if(obj.checkconnectivity()){
                    val dialog=ProgressDialog(this@LoginActivity)
                    dialog.setTitle("Logging in ...")
                    dialog.setCancelable(false)
                    dialog.create()
                    dialog.show()
                    mauth=FirebaseAuth.getInstance()
                    mauth.signInWithEmailAndPassword(emailtext.text.toString(),passwordtext.text.toString())
                        .addOnSuccessListener {
                            val uid=mauth.uid
                            val sharedpreferences=getSharedPreferences("user",Context.MODE_PRIVATE).edit()
                            sharedpreferences.putString("uid",uid)
                            sharedpreferences.apply()
                            sharedpreferences.commit()
                            val intent=Intent(this@LoginActivity,MainActivity::class.java)
                            startActivity(intent)
                            dialog.dismiss()
                        }
                        .addOnFailureListener {
                            dialog.dismiss()
                            Toast.makeText(this@LoginActivity,it.message.toString(),Toast.LENGTH_LONG).show()
                        }
                }
                else{
                    obj.showdialog()
                }

            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this@LoginActivity)
    }
}
