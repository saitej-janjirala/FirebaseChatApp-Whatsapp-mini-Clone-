package com.saitejajanjirala.mychatapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.utils.Connectivity
import com.saitejajanjirala.mychatapp.utils.Utils
import com.saitejajanjirala.mychatapp.utils.Keys
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.progress_layout
import kotlinx.android.synthetic.main.activity_splash_screen.*

class LoginActivity : AppCompatActivity() {
    var phoneNumber: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enter_number_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                phoneNumber = enter_number_edit_text.text.toString()
                val status = validateUsingLibPhoneNumber(phoneNumber)
                if (!status) {
                    enter_number_layout.error = "Enter valid phone number"
                } else {
                    enter_number_layout.isErrorEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        get_otp_button.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            if (enter_number_layout.isErrorEnabled) {
                Utils.showSnackBar(this, toplayout, "Enter proper 10 digit number")
            } else {
                val cN = Connectivity(this)
                if (cN.checkConnectivity()) {
                    val intent = Intent(this@LoginActivity, OtpActivity::class.java)
                    intent.putExtra(Keys.NUMBER, "+91$phoneNumber")
                    startActivityForResult(intent, Keys.OTP_REQUEST_CODE)
                } else {
                    cN.showDialog()
                }
            }
        }
    }

    private fun validateUsingLibPhoneNumber(phNumber: String): Boolean {
        return phNumber != null && phNumber.isDigitsOnly() && phNumber.length == 10
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ActivityCompat.finishAffinity(this@LoginActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            afterLogin()
        }
        else{
            enter_number_edit_text.requestFocus()
        }
    }
    private fun afterLogin(){
        get_otp_button.isEnabled=false
        progress_layout.visibility=View.VISIBLE
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db=FirebaseFirestore.getInstance()
        uid?.let {
            db.collection(Keys.USERS).document(it).get()
                .addOnSuccessListener {docSnap->
                    progress_layout.visibility= View.GONE
                    val userName=docSnap.get(Keys.USER_NAME)
                    if(userName==null ){
                        startActivity(Intent(this@LoginActivity,EnternameActivity::class.java))
                        finish()
                    }
                    else{
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener {ex->
                    progress_layout.visibility= View.GONE
                    Toast.makeText(this@LoginActivity,ex.message, Toast.LENGTH_LONG).show()

                }
        }
    }

}
