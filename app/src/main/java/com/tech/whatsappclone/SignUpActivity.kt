package com.tech.whatsappclone

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()  // hide the toolbar in activity
        auth = FirebaseAuth.getInstance()

        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setTitle("Creating Account...")
        mProgressDialog.setMessage("We're creating your account.")

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnGoogle.setOnClickListener {
            signInGoogle()
        }

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        binding.btnSignup.setOnClickListener {

            if(binding.etemail.text.toString().isEmpty()|| binding.etemail.text.toString() == ""){
                binding.etemail.error = "Enter your email"
                return@setOnClickListener
            }
            if(binding.etpassword.text.toString().isEmpty()|| binding.etpassword.text.toString() == ""){
                binding.etpassword.error = "Enter your password"
                return@setOnClickListener
            }
            if(binding.etuserName.text.toString().isEmpty()|| binding.etuserName.text.toString() == ""){
                binding.etuserName.error = "Enter your Username"
                return@setOnClickListener
            }
            mProgressDialog.show()
            auth.createUserWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etpassword.text.toString()
            ).addOnCompleteListener(this) { task ->


                if (task.isSuccessful) {
                    val userSignup = UserModel.UserSignup(
                        binding.etuserName.text.toString(),
                        binding.etemail.text.toString(),
                        binding.etpassword.text.toString()
                    )

                    val uid = auth.uid
                    if (uid != null) {
                        Log.d("@@@@", uid)
                        database.reference.child("Users").child(uid).setValue(userSignup)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "User Created Successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mProgressDialog.dismiss()
                                Log.d("@@@@", "success")
                                binding.etemail.setText("")
                                binding.etpassword.setText("")
                                binding.etuserName.setText("")
                            }.addOnFailureListener {
                                Log.d("@@@@", "failed")
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
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
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
                startActivity(Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT))
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
