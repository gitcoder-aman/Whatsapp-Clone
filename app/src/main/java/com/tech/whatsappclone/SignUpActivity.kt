package com.tech.whatsappclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.Model.UserSignup
import com.tech.whatsappclone.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()  // hide the toolbar in activity

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Creating Account...")
        mProgressDialog.setMessage("We're creating your account.")


        auth = Firebase.auth
        database = Firebase.database.reference

        binding.btnSignup.setOnClickListener{
            mProgressDialog.show()
            auth.createUserWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etpassword.text.toString()
            ).addOnCompleteListener(this) {task->


                    if (task.isSuccessful) {
                        val userSignup = UserSignup(
                            binding.etuserName.text.toString(),
                            binding.etemail.text.toString(),
                            binding.etpassword.text.toString()
                        )

                        val uid = Firebase.auth.uid
                        if (uid != null) {
                            Log.d("@@@@",uid)
                            database.child("Users").child(uid).setValue(userSignup).addOnSuccessListener{
                                Toast.makeText(
                                    this,
                                    "User Created Successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mProgressDialog.dismiss()
                                Log.d("@@@@","success")
                                binding.etemail.setText("")
                                binding.etpassword.setText("")
                                binding.etuserName.setText("")
                            }.addOnFailureListener {
                                Log.d("@@@@","failed")
                                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        mProgressDialog.dismiss()
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.alreadyAccount.setOnClickListener {
            finish()
            startActivity(Intent(this,SignInActivity::class.java))
        }
    }
}