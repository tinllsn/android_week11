package com.example.week11

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.week11.R
import com.example.week11.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date

private lateinit var binding: ActivityLoginBinding
class Login : AppCompatActivity() {
    var mAuth: FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth= FirebaseAuth.getInstance()
        signInAnonymously()
    }

    fun signInAnonymously(){
        mAuth!!.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(applicationContext, "Authentication success.",
                        Toast.LENGTH_SHORT).show()
                    val user = mAuth!!.getCurrentUser()

                } else {
                    Toast.makeText(applicationContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }

    fun buRegisterEvent(view: View){


        val userData=UserData(this)
        userData.savePhone(binding.etPhoneNumber.text.toString())

        val df =SimpleDateFormat("yyyy/MMM/dd HH:MM:ss")
        val date =Date()
        // save to database
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("Users").child(binding.etPhoneNumber.text.toString()).child("request").setValue(df.format(date).toString())
        mDatabase.child("Users").child(binding.etPhoneNumber.text.toString()).child("Finders").setValue(df.format(date).toString())

//        Toast.makeText(this,"tc",Toast.LENGTH_SHORT).show()
        finish()

    }
}