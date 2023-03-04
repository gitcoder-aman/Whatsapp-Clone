package com.tech.whatsappclone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.tech.whatsappclone.Model.UserModel
import com.tech.whatsappclone.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySettingBinding
    private lateinit var storage:FirebaseStorage
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.backArrow.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.plus.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*" //*/* all show type of files
            launcher.launch(intent)
        }
        //setting profile image set on Imageview from database
        database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userModel: UserModel? = snapshot.getValue(UserModel::class.java)

                    Picasso.get()
                                .load(userModel?.getProfilePic())
                                .placeholder(R.drawable.profile)
                                .into(binding.profileImage)

                    binding.etAbout.setText(userModel?.getAbout())
                    binding.etUsername.setText(userModel?.getUserName())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("@@img","onCancelled"+error.message)
                }

            })

        //save about,username after click on save btn
        binding.saveBtn.setOnClickListener {
            val about = binding.etAbout.text.toString()
            val username = binding.etUsername.text.toString()

            val obj:HashMap<String, Any> = HashMap()
            obj["userName"] = username
            obj["about"] = about

            database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                .updateChildren(obj).addOnSuccessListener {
                    Toast.makeText(this, "Save successful.", Toast.LENGTH_SHORT).show()
                }
        }

    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        if(result.resultCode == Activity.RESULT_OK){
            if(result.data != null){
                val sFile: Uri? = result.data!!.data
                binding.profileImage.setImageURI(sFile)

                //store data on firebase storage
                val reference = storage.reference.child("profile_pictures")
                    .child(FirebaseAuth.getInstance().uid.toString())
                    reference.putFile(sFile!!).addOnSuccessListener {

                        //upload on Firebase database for accessing the image on RecyclerView
                        reference.downloadUrl.addOnSuccessListener {uri->
                            database.reference.child("Users").child(FirebaseAuth.getInstance().uid.toString())
                                .child("profilePic").setValue(uri.toString())
                        }
                        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed to Profile update on firebase.", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
    }
}