package com.tech.whatsappclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.databinding.ActivitySignInBinding


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Login...")
        mProgressDialog.setMessage("Login to your account.")

        // Initialize sign in options the client-id is copied form google-services.json file
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.btnGoogle.setOnClickListener {
           signInGoogle()
        }

        binding.btnSignin.setOnClickListener {

            if(binding.etemail.text.toString().isEmpty()|| binding.etemail.text.toString() == ""){
                binding.etemail.error = "Enter your email"
                return@setOnClickListener
            }
            if(binding.etpassword.text.toString().isEmpty()|| binding.etpassword.text.toString() == ""){
                binding.etpassword.error = "Enter your password"
                return@setOnClickListener
            }
            mProgressDialog.show()
            auth.signInWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etpassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    mProgressDialog.dismiss()
                    startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
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
    private fun signInGoogle() {
        Log.d("@@@@L","SignInGoogle")
        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.d("@@@@", "registerActivityResult launch")
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            try {
//                //Google Sign In was successful, authenticate with firebase
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
//                Log.d("@@@@","Google sign in successful"+ account.id)
                Toast.makeText(this, "Google sign in successful", Toast.LENGTH_SHORT).show()
                updateUI(account)
            } catch (e: ApiException) {
                //Google sign In failed, update UI appropriately
                Log.d("@@@@", "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("@@@@", "account Handle not successful")
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Sign in to "+account.displayName.toString(), Toast.LENGTH_SHORT).show()
                Log.d("@@@@", "signInWithCredential success")
                Log.d("@@@@", account.displayName.toString())
                Log.d("@@@@", account.email.toString())

                //store data in database of google login
                val user : FirebaseUser? = auth.currentUser
                val userModel = UserModel()
                userModel.setUserId(user?.uid)
                userModel.setUserName(user?.displayName)
//                userModel.setProfilePic(user?.photoUrl.toString())

                database.reference.child("Users").child(user?.uid.toString()).setValue(userModel)
            } else {
                Log.d("@@@@", "signInWithCredential failed")
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}