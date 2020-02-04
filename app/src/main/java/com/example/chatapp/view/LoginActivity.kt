package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.data.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var verificationIdCode: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        login_button.setOnClickListener{

            var numberPhone = number_phone.text.toString().trim()
            if (numberPhone.startsWith("7")){
                numberPhone = "+$numberPhone"
            } else if (numberPhone.startsWith("8")){
                numberPhone = numberPhone.substring(1)
                numberPhone = "+7$numberPhone"
            }
            if ( numberPhone.length in 11..12) {
                login_button.isEnabled = false
                login_progress_bar.visibility = ProgressBar.VISIBLE
                sendVerificationCode(numberPhone)

            }else{
                val toast = Toast.makeText(this@LoginActivity,
                    "Неверный номер",
                    Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        verify_phone_button.setOnClickListener{
            login_progress_bar.visibility = ProgressBar.VISIBLE
            verifySignInCode()
        }
    }


    private fun sendVerificationCode(numberPhone: String){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(numberPhone,
            60,
            TimeUnit.SECONDS,
            this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    val toast = Toast.makeText(this@LoginActivity,
                        "Автоматический вход",
                        Toast.LENGTH_SHORT)
                    toast.show()
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    login_progress_bar.visibility = ProgressBar.INVISIBLE
                    val toast = Toast.makeText(this@LoginActivity,
                        "Что-то пошло не так..",
                        Toast.LENGTH_SHORT)
                    toast.show()
                    login_button.isEnabled = true
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    login_progress_bar.visibility = ProgressBar.INVISIBLE
                    verificationIdCode = verificationId
                    val toast = Toast.makeText(this@LoginActivity,
                        "Сообщение отправлено..",
                        Toast.LENGTH_SHORT)
                    toast.show()
                    login_button.isEnabled= true
                }
            })

    }

    private  fun verifySignInCode() {

        val credential = PhoneAuthProvider
            .getCredential(verificationIdCode,
                verify_phone_code.text.toString().trim())
        signInWithPhoneAuthCredential(credential)
    }

    companion object{

    const val USER_UID = "USER_UID"

    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    FirebaseDatabase.getInstance().reference.child("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .child("userName")
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {}

                        override fun onDataChange(p0: DataSnapshot) {
                            val userName = if (p0.value != null){
                                p0.value.toString()
                            }else{null}

                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(
                                        User(
                                            FirebaseAuth.getInstance().currentUser?.phoneNumber,
                                            userName,
                                            FirebaseAuth.getInstance().currentUser?.uid,
                                            FirebaseInstanceId.getInstance().getToken()
                                        )
                                    )

                            if (userName !=null){
                                val intent = Intent(this@LoginActivity,
                                    MainActivity::class.java)
                                login_progress_bar.visibility = ProgressBar.INVISIBLE
                                startActivity(intent)
                                finish()
                            }else{
                                val intent = Intent(this@LoginActivity,
                                    AddNameUserActivity::class.java)
                                val uid = FirebaseAuth.getInstance().uid?:return
                                intent.putExtra(USER_UID,uid)
                                login_progress_bar.visibility = ProgressBar.INVISIBLE
                                startActivity(intent)
                                finish()
                            }
                        }
                    })
                    // Sign in success, update UI with the signed-in user's information
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        login_progress_bar.visibility = ProgressBar.INVISIBLE
                        val toast = Toast.makeText(this@LoginActivity,
                            "Неверный код",
                            Toast.LENGTH_SHORT)
                            toast.show()
                    }
                }
            }
    }
}
