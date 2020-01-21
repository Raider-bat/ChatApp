package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
            if ( numberPhone.length >10) {
                sendVerificationCode(numberPhone)
            }
        }

        verify_phone_button.setOnClickListener{
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

                    Toast.makeText(this@LoginActivity,"Автоматический вход", Toast.LENGTH_SHORT).show()
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    Toast.makeText(this@LoginActivity,"Что-то пошло не так..", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationIdCode = verificationId
                    Toast.makeText(this@LoginActivity,"Сообщение отправлено..", Toast.LENGTH_SHORT).show()
                }
            })

    }

    private  fun verifySignInCode() {

        val credential = PhoneAuthProvider.getCredential(verificationIdCode, verify_phone_code.text.toString().trim())
        signInWithPhoneAuthCredential(credential)
    }


    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val userName: String = user_name.text.toString().trim()

                    FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (userName.isNotEmpty()){

                                val updateName =  UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                                FirebaseAuth.getInstance().currentUser?.updateProfile(updateName)
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(User(FirebaseAuth.getInstance().currentUser?.phoneNumber,
                                        userName,
                                        FirebaseAuth.getInstance().currentUser?.uid,
                                        FirebaseInstanceId.getInstance().getToken()))

                            }else{
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(User(FirebaseAuth.getInstance().currentUser?.phoneNumber,
                                        FirebaseAuth.getInstance().currentUser?.displayName,
                                        FirebaseAuth.getInstance().currentUser?.uid,
                                        FirebaseInstanceId.getInstance().getToken()))
                            }
                        }
                    })
                    if (userName.isNotEmpty()){

                        val updateName =  UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                        FirebaseAuth.getInstance().currentUser?.updateProfile(updateName)
                    }
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this@LoginActivity,"Неверный код", Toast.LENGTH_SHORT).show()

                    }
                }
            }
    }
}
