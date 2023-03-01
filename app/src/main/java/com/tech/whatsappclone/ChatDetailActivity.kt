package com.tech.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.tech.whatsappclone.databinding.ActivityChatDetailBinding

class ChatDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth :FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        val senderId : String = auth.uid.toString()
        val receiverId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("username")
        val profilePic = intent.getStringExtra("profilePic")

        binding.txtUsername.text = userName
        Picasso.get().load(profilePic)
            .placeholder(R.drawable.profile)
            .into(binding.profileImage)

        binding.backArrowBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}