package com.tech.whatsappclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Login...")
        mProgressDialog.setMessage("Login to your account.")

        binding.btnSignin.setOnClickListener {
            mProgressDialog.show()
            auth.signInWithEmailAndPassword(binding.etemail.text.toString(),binding.etpassword.text.toString()).addOnCompleteListener {
                if(it.isSuccessful){
                    mProgressDialog.dismiss()
                    startActivity(Intent(this,MainActivity::class.java));
                }else{
                    mProgressDialog.dismiss()
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.clickSignup.setOnClickListener {
            this.finish()
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        if(auth.currentUser != null){
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}