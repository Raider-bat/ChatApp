package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_name_user.*

class AddNameUserActivity : AppCompatActivity() {
    lateinit var userUid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_name_user)
        userUid = intent.getStringExtra(LoginActivity.USER_UID)


        set_userName_button.setOnClickListener {
            addUserNameInDataBase()
        }
    }

    private fun addUserNameInDataBase(){
        val userName = user_name.text.toString().trim()
        if (userName.length in 3..23){
            FirebaseDatabase.getInstance().reference.child("Users").orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.value != null){
                        val toast = Toast.makeText(this@AddNameUserActivity, "Подобный никнейм уже существует",Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.CENTER,0,0)
                        toast.show()
                    }else{
                        FirebaseDatabase.getInstance().reference.child("Users").child(userUid).child("userName").setValue(userName).addOnCompleteListener {
                            for ( i in 1..2){
                                val updateName =  UserProfileChangeRequest.Builder().setDisplayName(userName).build()
                                FirebaseAuth.getInstance().currentUser?.updateProfile(updateName)
                            }

                            val intent = Intent(this@AddNameUserActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })

         }else{
            val toast = Toast.makeText(this, "Неверный никнейм",Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER,0,0)
            toast.show()
        }
    }
}
