package com.tech.whatsappclone

import android.R
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var oneTapClient: SignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    lateinit var signInRequest: BeginSignInRequest
    private val REQUEST_SIGN_IN : Int = 65


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Login...")
        mProgressDialog.setMessage("Login to your account.")

        // Initialize sign in options the client-id is copied form google-services.json file
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("155031488126-sh5rri1recu1ki46goqv9r4qogdh9oed.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnGoogle.setOnClickListener {
            // Initialize sign in intent
            val signInIntent : Intent = mGoogleSignInClient.signInIntent
            // Start activity for result
            startActivityForResult(signInIntent,REQUEST_SIGN_IN)
        }

        binding.btnSignin.setOnClickListener {
            mProgressDialog.show()
            auth.signInWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etpassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    mProgressDialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java));
                } else {
                    mProgressDialog.dismiss()
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.clickSignup.setOnClickListener {
            this.finish()
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    //Google Sign in code
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode == REQUEST_SIGN_IN){
           val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                //Google Sign In was successful, authenticate with firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
//                Log.d("@@@@","Google sign in successful"+ account.id)
                Toast.makeText(this, "Google sign in successful", Toast.LENGTH_SHORT).show()
                firebaseAuthWithGoogle(account)
            }catch (e:ApiException ){
                //Google sign In failed, update UI appropriately
                Log.d("@@@@","Google sign in failed",e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val idToken = account.idToken
        if (idToken != null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult?> { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("@@@@", "signInWithCredential:success")
                            val intent = Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            val user = auth.currentUser
//                            updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("@@@@", "signInWithCredential:failure", task.exception)
//                            updateUI(null)
                        }
                    })
        }
    }

}