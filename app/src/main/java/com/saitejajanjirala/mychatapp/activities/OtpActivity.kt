package com.saitejajanjirala.mychatapp.activities

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.saitejajanjirala.mychatapp.R
import com.saitejajanjirala.mychatapp.model.User
import com.saitejajanjirala.mychatapp.utils.Connectivity
import com.saitejajanjirala.mychatapp.utils.Utils
import com.saitejajanjirala.mychatapp.utils.Keys
import kotlinx.android.synthetic.main.activity_otp.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {
    var otp = ""
    var phoneNumber = ""
    private var verificationId = ""
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var firebaseAuth: FirebaseAuth
    var count = 60
    val countDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onFinish() {
            resend_otp.text = resources.getString(R.string.resend_otp)
        }

        override fun onTick(p0: Long) {
            count--
            resend_otp.text = "${resources.getString(R.string.resend_in)} ${count}s"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        getIntentData()
        setListeners()
    }

    private fun getIntentData() {
        phoneNumber = intent.getStringExtra(Keys.NUMBER)!!
        if (phoneNumber == null) {
            finishAndCancel()
        } else {
            edit_number.text = phoneNumber
            firebaseAuth = FirebaseAuth.getInstance()
            getOtp()

        }
    }

    private fun setListeners() {

        edit_number.setOnClickListener {
            finishAndCancel()
        }

        resend_otp.setOnClickListener {
            if (resend_otp.text == resources.getString(R.string.resend_otp)) {
                getOtp()
            }
        }

        otp_number_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                otp = otp_number_text.text.toString()
                if (otp.isDigitsOnly() && otp.trim().length == 6) {
                    otp_number_layout.isErrorEnabled = false
                } else {
                    otp_number_layout.error = "Enter proper 6 digit otp"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        verify_button.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)

            if (!otp_number_layout.isErrorEnabled) {
                val cN = Connectivity(this)
                if (cN.checkConnectivity()) {
                    progress_layout.visibility = View.VISIBLE
                    val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener {
                            countDownTimer.cancel()
                            resend_otp.text = resources.getString(R.string.resend_otp)
                            setNumberData()
                        }
                        .addOnFailureListener {
                            progress_layout.visibility = View.GONE
                            Toast.makeText(this@OtpActivity, it.message, Toast.LENGTH_LONG)
                                .show()
                            countDownTimer.cancel()
                            resend_otp.text = resources.getString(R.string.resend_otp)
                        }
                } else {
                    cN.showDialog()
                }
            } else {
                Utils.showSnackBar(this, top_layout, "Enter proper 6 digit otp")
            }
        }
    }

    private fun setNumberData() {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser.uid
        db.collection(Keys.USERS).document(uid)
            .get()
            .addOnSuccessListener {
                if(it[Keys.PHONE_NUMBER]!=null && it[Keys.UID]!=null){
                    db.collection(Keys.USERS).document(uid).update(Keys.ACTIVE,true)
                        .addOnSuccessListener {
                            progress_layout.visibility = View.GONE
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        .addOnFailureListener {
                            progress_layout.visibility = View.GONE
                            finishAndCancel()
                        }
                }
                else{
                    db.collection(Keys.USERS).document(uid).set(User(phonenumber = phoneNumber,uid = uid,active = true))
                        .addOnSuccessListener {
                            progress_layout.visibility = View.GONE
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        .addOnFailureListener {
                            progress_layout.visibility = View.GONE
                            finishAndCancel()
                        }
                }


            }
            .addOnFailureListener {

            }


    }

    private fun finishAndCancel() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun getOtp() {
        progress_layout.visibility = View.VISIBLE
        val mcallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                this@OtpActivity.verificationId = verificationId
                this@OtpActivity.forceResendingToken = forceResendingToken
                count = 60
                countDownTimer.start()
                progress_layout.visibility = View.GONE

            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
        if (forceResendingToken != null) {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mcallbacks)
                .setForceResendingToken(forceResendingToken!!)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } else {
            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mcallbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    override fun onBackPressed() {
    }
}